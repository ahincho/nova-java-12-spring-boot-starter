# Nova Spring Boot Starter

The meta-starter. One dependency brings in Spring Boot web, Jackson and
Actuator, the framework-free Nova libraries, and every Nova starter that
is safe to have without configuration — so a new service starts with the
API contract, layered errors, masking, observability, secrets, dates and
mapping in place instead of assembling them.

## What it pulls in

| From Spring Boot | From Nova | Version |
|---|---|---|
| `spring-boot-starter` | `nova-date-utils` | 1.0.2 |
| `spring-boot-starter-webmvc` | `nova-mapper-utils` | 1.0.2 |
| `spring-boot-starter-jackson` | `nova-mask-spring-boot-starter` | 3.0.1 |
| `spring-boot-starter-actuator` | `nova-api-standard-spring-boot-starter` | 3.0.1 |
| | `nova-observability-spring-boot-starter` | 3.0.0 |
| | `nova-secrets-spring-boot-starter` | 1.2.0 |

**A starter is in only if it changes nothing until it is configured**
([ADR-052](https://github.com/ahincho/nova-shared-01-docs/blob/main/adrs/java/ADR-052-meta-extension-de-quarkus.md)).
That is why observability is in since its 3.0.0, which exports nothing
until an OTLP endpoint is set, and why secrets are in: without
`nova.secrets.import` they read no store. `nova-idempotency-spring-boot-starter`
stays out: it switches itself on and needs the table of its JDBC store.
Declare it separately when a service needs it.

**The versions are written in this build, not taken from
`nova-spring-boot-bom`.** The BOM manages this artifact, so importing the
BOM here always left it one release behind: 1.0.4 imported BOM 2.0.0 and
handed out the 2.x starters. Spring Boot comes with its own BOM, and the
security pins the Nova BOM sets — Tomcat 11.0.26 today — travel with this
artifact too.

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
    implementation("pe.edu.nova.java.starters:nova-spring-boot-starter:2.0.0")
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
the platform wraps them in `ApiResponse<T>`; thrown errors are answered
by layer, with the catalog code and `metadata.traceId`; annotated fields
are masked in logs.

To export telemetry, set the collector:

```yaml
nova:
  observability:
    otlp:
      endpoint: http://otel-collector:4318
```

or `OTEL_EXPORTER_OTLP_ENDPOINT` in the environment. Without it, metrics,
traces and log correlation still work inside the service; nothing leaves it.

## Migrating to 2.0.0

| Before (1.x) | From 2.0.0 | What to do |
|---|---|---|
| the starters came at the versions of BOM 2.0.0 (API standard 2.x) | API standard and mask 3.0.1, with the layered errors of ADR-031 | follow [«Migrating to 3.0.0»](https://github.com/ahincho/nova-java-08-commons-spring-boot-starter#migrating-to-300) of the API standard starter |
| observability was a separate dependency | it comes with the meta-starter, at 3.0.0 | drop the separate declaration, or keep it at 3.0.0; set the OTLP endpoint if the service exports |
| secrets were a separate dependency | they come with the meta-starter, at 1.2.0 | drop the separate declaration, or keep it at 1.2.0 |
| no Tomcat pin unless the Nova BOM was imported | Tomcat 11.0.26 | nothing |

A service that already imports `nova-spring-boot-bom` 3.x was receiving
the 3.x starters through the BOM; for it, the change is the two starters
that are now included.

## Starting from scratch

Rather than adding this to an empty project, start from a service
template ([ADR-051](https://github.com/ahincho/nova-shared-01-docs/blob/main/adrs/shared/ADR-051-plantillas-de-servicio.md)),
once `nova-template-01-spring-boot-service` is published.

## Requirements

Java 25, Spring Boot 4.

## License

Eclipse Public License 2.0 — see [LICENSE](LICENSE).

Copyright © 2026 Angel Hincho.
