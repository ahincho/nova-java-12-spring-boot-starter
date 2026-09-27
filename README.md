# Nova Spring Boot Starter

The meta-starter. One dependency brings in Spring Boot web, Jackson and
Actuator, every framework-free Nova library, and every Nova starter
already auto-configured — so a new service starts with the API contract,
masking, dates and mapping in place instead of assembling them.

## What it pulls in

| From Spring Boot | From Nova |
|---|---|
| `spring-boot-starter` | `nova-date-utils` |
| `spring-boot-starter-webmvc` | `nova-mapper-utils` |
| `spring-boot-starter-jackson` | `nova-mask-starter` |
| `spring-boot-starter-actuator` | `nova-api-standard-starter` |

Versions come from `nova-spring-boot-bom`, so an application declares none
of them.

## What it adds

| Class | Does |
|---|---|
| `@NovaSpringBootApplication` | Replaces `@SpringBootApplication`, adding the Nova component scan |
| `NovaApplication` | `run(...)` entry point |
| `NovaAutoConfiguration` | Registers the platform beans |
| `NovaEnvironmentPostProcessor` | Applies the platform's property defaults before the context starts |

## Install

Published to GitHub Packages, so the repository needs to be declared and
authenticated with a token that has `read:packages`.

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/ahincho/nova-java-12-spring-boot-starter")
        credentials {
            username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
            password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("pe.edu.nova.java.starters:nova-spring-boot-starter:0.1.0-SNAPSHOT")
}
```

## Use

```java
import pe.edu.nova.java.starters.boot.NovaSpringBootApplication;

@NovaSpringBootApplication
public class Application {
    public static void main(String[] args) {
        NovaApplication.run(Application.class, args);
    }
}
```

That is the whole bootstrap. Controllers can return domain objects and
the platform wraps them in `ApiResponse<T>`; thrown exceptions become
`ApiError`; annotated fields are masked in logs.

Observability is a separate dependency on purpose — not every service
wants an OTLP exporter. Add
[nova-java-observability-spring-boot-starter](https://github.com/ahincho/nova-java-09-observability-spring-boot-starter)
when it does.

## Starting from scratch

Rather than adding this to an empty project, generate one:

- [nova-java-spring-boot-archetype](https://github.com/ahincho/nova-java-17-spring-boot-archetype) — Maven
- [nova-java-spring-boot-gradle-plugin](https://github.com/ahincho/nova-java-16-spring-boot-gradle-plugin) — Gradle conventions

## Requirements

Java 25, Spring Boot 4.

## License

Eclipse Public License 2.0 — see [LICENSE](LICENSE).

Copyright © 2026 Angel Hincho.
