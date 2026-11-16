import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions

plugins {
    `java-library`
    id("signing")
    id("com.vanniktech.maven.publish") version "0.36.0"
}

val projectGroup: String by project
val projectVersion: String by project

group = projectGroup
version = projectVersion
description = "Etheros 通用业务结果类型：Result / ResultCode"

val jspecifyVersion: String by project
val lombokVersion: String by project
val junitVersion: String by project

java {
    // 构建用 JDK 21，产物锁在 Java 8：纯 Java 类型，Minecraft 插件等 Java 8 工程也能用
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.compileJava {
    options.release.set(8)
    // --release 8 在 JDK 21 上会提示“源/目标值 8 已过时”，抑制该噪声
    options.compilerArgs.add("-Xlint:-options")
}

// 单测用 JUnit 6，需要 Java 17 以上的测试字节码；不随构件发布
tasks.compileTestJava {
    options.release.set(17)
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
}

dependencies {
    // 空值契约注解出现在公共 API 签名里，随构件传递给下游
    api("org.jspecify:jspecify:$jspecifyVersion")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

// 发布前预演仓库：产物与 POM 先落到 build/staging-repo，用于发布前不触网的本地校验
extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "staging"
            url = uri(layout.buildDirectory.dir("staging-repo"))
        }
    }
}

// Maven Central 发布：坐标、POM 元信息与签名
mavenPublishing {
    // 上传到 Central Portal 并自动 release，无需再进网页点发布
    publishToMavenCentral(true)
    signAllPublications()

    coordinates("cn.etheros", "etheros-result", project.version.toString())

    pom {
        name.set("etheros-result")
        description.set(project.description)
        url.set("https://github.com/EtherosGroup/etheros-result")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }
        developers {
            developer {
                id.set("Etheros Group")
                name.set("Etheros Group")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/EtherosGroup/etheros-result.git")
            developerConnection.set("scm:git:https://github.com/EtherosGroup/etheros-result.git")
            url.set("https://github.com/EtherosGroup/etheros-result")
        }
    }
}

// 本地用 gpg 命令签名，凭据取自 ~/.gradle/gradle.properties
// CI 由 signingInMemoryKey 系列属性走内存签名
if (providers.gradleProperty("signingInMemoryKey").orNull == null) {
    signing {
        useGpgCmd()
    }
}

// 元数据任务依赖 javadoc jar，避免并行时读取未生成的产物
afterEvaluate {
    tasks.matching { it.name == "generateMetadataFileForMavenPublication" }.configureEach {
        dependsOn(tasks.matching { it.name == "plainJavadocJar" })
    }
}
