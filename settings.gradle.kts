import org.gradle.api.initialization.resolve.RepositoriesMode

// etheros-result 独立构建入口：通用业务结果类型（Result / ResultCode）
// 坐标与版本见 gradle.properties 的 projectGroup / projectVersion

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    // 禁止子工程自行声明仓库，避免同一定义出现多个来源
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "etheros-result"
