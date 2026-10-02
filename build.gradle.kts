plugins {
    id("java-library")
    id("maven-publish")
    checkstyle
    id("net.nemerosa.versioning") version "4.0.1"
    id("signing")
    id("org.owasp.dependencycheck") version "12.2.2"
    id("org.cyclonedx.bom") version "3.2.4"
}

versioning {
    releaseMode = "snapshot"
    displayMode = "snapshot"
    releaseBuild = false
}

group = "pe.edu.nova.java.starters"
version = findProperty("version") as String

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    // Internal Nova Platform dependencies (each lives in its own repo/package).
    // GITHUB_TOKEN cannot read packages from another repo, so this needs a PAT
    // (falls back to GITHUB_TOKEN for local/manual builds where only that is set).
    val readToken = System.getenv("NOVA_PACKAGES_READ_TOKEN") ?: System.getenv("GITHUB_TOKEN")
    maven {
        name = "NovaBom"
        url = uri("https://maven.pkg.github.com/ahincho/nova-java-13-bom")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = readToken
        }
    }
    maven {
        name = "NovaDateUtils"
        url = uri("https://maven.pkg.github.com/ahincho/nova-java-02-date-utils")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = readToken
        }
    }
    maven {
        name = "NovaMapperUtils"
        url = uri("https://maven.pkg.github.com/ahincho/nova-java-03-mapper-utils")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = readToken
        }
    }
    maven {
        name = "NovaCommonsSpringBootStarter"
        url = uri("https://maven.pkg.github.com/ahincho/nova-java-08-commons-spring-boot-starter")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = readToken
        }
    }
    maven {
        name = "NovaObservabilitySpringBootStarter"
        url = uri("https://maven.pkg.github.com/ahincho/nova-java-09-observability-spring-boot-starter")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = readToken
        }
    }
    maven {
        name = "NovaSecrets"
        url = uri("https://maven.pkg.github.com/ahincho/nova-java-23-secrets")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = readToken
        }
    }
}

val junitVersion = "6.0.3"
val jqwikVersion = "1.9.3"

// La misma versión de Spring Boot que el resto de la plataforma.
val springBootVersion = "4.0.8"

// Las versiones de Nova van escritas aquí y no salen de nova-spring-boot-bom (ADR-052). El BOM
// gestiona a este meta-starter, así que importarlo lo dejaba siempre una versión atrás: la 1.0.4
// importaba el BOM 2.0.0 y repartía los starters 2.x.
val dateUtilsVersion = "1.0.2"
val mapperUtilsVersion = "1.0.2"
val commonsStartersVersion = "3.0.1"
val observabilityStarterVersion = "3.0.0"
val secretsVersion = "1.2.0"

dependencies {
    // Sin el BOM de Nova, los parches de seguridad que él fija viajan con el meta-starter:
    // Spring Boot 4.0.8 trae Tomcat 11.0.24, que tiene CVE-2026-68525, CVE-2026-65905 y
    // CVE-2026-65182, corregidas en la 11.0.25.
    constraints {
        api("org.apache.tomcat.embed:tomcat-embed-core:11.0.26") {
            because("CVE-2026-68525, CVE-2026-65905, CVE-2026-65182 require 11.0.25+")
        }
        api("org.apache.tomcat.embed:tomcat-embed-websocket:11.0.26") {
            because("Same CVEs in 11.0.24")
        }
        api("org.apache.tomcat.embed:tomcat-embed-el:11.0.26") {
            because("Same CVEs in 11.0.24")
        }
        // Llega con el exportador OTLP del starter de observabilidad, como en ese starter.
        api("org.jetbrains.kotlin:kotlin-stdlib:2.4.0") {
            because("CVE-2026-53914 CRITICAL 9.8 requires 2.4.0+")
        }
    }

    // Spring Boot, con las versiones de su propio BOM
    api(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-starter-webmvc")
    api("org.springframework.boot:spring-boot-starter-jackson")
    api("org.springframework.boot:spring-boot-starter-actuator")

    // Librerías puras de Nova
    api("pe.edu.nova.java.libs:nova-date-utils:$dateUtilsVersion")
    api("pe.edu.nova.java.libs:nova-mapper-utils:$mapperUtilsVersion")

    // Los starters de Nova que, sin configuración, no cambian el comportamiento del servicio
    // (ADR-052). La idempotencia queda fuera: se enciende sola y exige la tabla de su almacén.
    api("pe.edu.nova.java.starters:nova-mask-spring-boot-starter:$commonsStartersVersion")
    api("pe.edu.nova.java.starters:nova-api-standard-spring-boot-starter:$commonsStartersVersion")
    api("pe.edu.nova.java.starters:nova-observability-spring-boot-starter:$observabilityStarterVersion")
    api("pe.edu.nova.java.starters:nova-secrets-spring-boot-starter:$secretsVersion")

    // Test
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-launcher:$junitVersion")
    testImplementation("net.jqwik:jqwik:$jqwikVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:all", "-quiet")
        encoding = "UTF-8"
        charSet = "UTF-8"
    }
}

checkstyle {
    // Only lint production code. Test suites commonly rely on static-import
    // wildcards (org.junit.jupiter.api.Assertions.*, net.jqwik.api.*), which
    // is an accepted convention that would otherwise trip AvoidStarImport.
    sourceSets = listOf(project.sourceSets.main.get())
}

// Versiones parcheadas de dependencias que el OWASP gate marca con CVSS >= 7. Las cuatro
// llegan por la herramienta checkstyle; son las mismas que usan los demás starters.
// Verificadas contra la GitHub Advisory Database el 2026-09-27.
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.apache.httpcomponents" && requested.name.startsWith("httpcore")) {
            useVersion("4.4.16")
            because("CVE-2026-54428, CVE-2026-54399 require httpcore 4.4.16+")
        }
        if (requested.group == "org.apache.httpcomponents.core5" && requested.name.startsWith("httpcore5")) {
            useVersion("5.4.3")
            because("CVE-2026-54399 requires httpcore5 5.4.3+")
        }
        if (requested.group == "commons-beanutils" && requested.name == "commons-beanutils") {
            useVersion("1.11.0")
            because("CVE-2025-48734 requires commons-beanutils 1.11.0+")
        }
        if (requested.group == "org.codehaus.plexus" && requested.name == "plexus-utils") {
            useVersion("3.6.1")
            because("CVE-2025-67030 requires plexus-utils 3.6.1+")
        }
    }
}

dependencyCheck {
    // NVD_API_KEY / NOVA_OWASP_FAIL_ON_CVSS are injected by reusable-owasp-check.yml.
    // Locally (no env vars set) this defaults to "never fail" (11.0, matches plugin default)
    // and an empty NVD key (slower updates, acceptable for local dev).
    failBuildOnCVSS = (System.getenv("NOVA_OWASP_FAIL_ON_CVSS") ?: "11").toFloat()
    nvd.apiKey = System.getenv("NVD_API_KEY") ?: ""
    // reusable-owasp-check.yml restaura un mirror de NVD de menos de 24 horas. Sin estas
    // dos líneas el plugin lo ignora, sincroniza NVD entero y puede quedarse sin memoria.
    autoUpdate = false
    data.directory = System.getenv("NOVA_OWASP_DATA_DIR")
        ?: "${System.getProperty("user.home")}/.dependency-check-data"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ahincho/nova-java-12-spring-boot-starter")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

signing {
    val gpgKeyId: String? = System.getenv("GPG_SIGNING_KEY_ID")
    val gpgKey: String? = System.getenv("GPG_SIGNING_KEY")
    val gpgPassword: String? = System.getenv("GPG_SIGNING_PASSWORD")

    if (gpgKeyId != null && gpgKey != null) {
        useInMemoryPgpKeys(gpgKeyId, gpgKey, gpgPassword ?: "")
        sign(publishing.publications)
    }
}