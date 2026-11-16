# etheros-result

Etheros 通用业务结果类型：`Result` / `ResultCode` 及通用结果码。

[![ci](https://github.com/EtherosGroup/etheros-result/actions/workflows/ci.yml/badge.svg)](https://github.com/EtherosGroup/etheros-result/actions/workflows/ci.yml)

Maven Central：[`cn.etheros:etheros-result`](https://central.sonatype.com/artifact/cn.etheros/etheros-result)

纯 Java、零 Spring 依赖，从 etheros-web 的 `cn.etheros.web.common.result` 包抽出，独立发版。

## 坐标

```kotlin
implementation("cn.etheros:etheros-result:1.0.1")
```

- Java 8 字节码，任何 Java 8+ 工程可用（Spring Boot 服务、Minecraft 插件、CLI 都可以）
- 唯一传递依赖：`org.jspecify:jspecify`（空值契约注解，出现在公共 API 签名里）

## 内容

| 类型 | 说明 |
| --- | --- |
| `Result<T, E extends Enum<E> & ResultCode>` | 业务结果：数据 + 结果码；`of(data, code)` / `of(code)` 两个工厂方法 |
| `ResultCode` | 结果码接口，业务侧用枚举实现 |
| `CommonResultCode` | 通用成功码 `Success` |
| `VoidResult` | 无结果码场景的占位枚举 |

## 用法

```java
// 业务侧定义结果码
public enum AuthResultCode implements ResultCode {
    Success,
    AccountBanned,
    InvalidPassword
}

// 服务层返回
return Result.of(user, AuthResultCode.Success);
return Result.of(AuthResultCode.InvalidPassword);

// 调用方判读
Result<User, AuthResultCode> result = authService.login(...);
if (result.getResultCode() == AuthResultCode.Success) {
    User user = result.getData();
}
```

`data` 允许为 `null`（用 `Result.of(code)` 创建时恒为 `null`），`resultCode` 不允许为 `null`。

## 发布

发布到 Maven Central（Central Portal），版本号取 `gradle.properties` 的 `projectVersion`。

**发版流程**：改 `projectVersion` → 提交 → `./gradlew build` 通过 → 打同名标签（`v1.0.2`）→ 推送标签，CI 自动发布。

本机手动发布（凭据取自 `~/.gradle/gradle.properties`）：

```bash
# 发布前预演：产物与 POM 落到 build/staging-repo，不触网
./gradlew publishAllPublicationsToStagingRepository

# 正式发布：上传并自动 release，不可撤回
./gradlew publishAndReleaseToMavenCentral
```

CI 发布（`.github/workflows/publish.yml`）需要仓库 Secrets：

| Secret | 内容 |
| --- | --- |
| `MAVEN_CENTRAL_USERNAME` | Central Portal 用户 token 用户名 |
| `MAVEN_CENTRAL_PASSWORD` | Central Portal 用户 token 密码 |
| `GPG_SIGNING_KEY` | ASCII-armored GPG 私钥 |
| `GPG_SIGNING_PASSWORD` | 私钥口令 |

## 构建

```bash
./gradlew build            # 编译 + 单测 + 打包
./gradlew javadoc          # 生成 API 文档
```
