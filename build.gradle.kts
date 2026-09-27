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
}

val junitVersion = "6.0.3"
val jqwikVersion = "1.9.3"

dependencies {
    // BOM — centralizes versions for Spring Boot and internal libs
    api(platform("pe.edu.nova.java:nova-spring-boot-bom:2.0.0"))

    // Spring Boot starters (version from BOM)
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-starter-webmvc")
    api("org.springframework.boot:spring-boot-starter-jackson")
    api("org.springframework.boot:spring-boot-starter-actuator")

    // Internal Nova Platform libraries (version from BOM)
    api("pe.edu.nova.java.libs:nova-date-utils")
    api("pe.edu.nova.java.libs:nova-mapper-utils")

    // Internal Nova Platform starters (version from BOM)
    api("pe.edu.nova.java.starters:nova-mask-spring-boot-starter")
    api("pe.edu.nova.java.starters:nova-api-standard-spring-boot-starter")

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

dependencyCheck {
    // NVD_API_KEY / NOVA_OWASP_FAIL_ON_CVSS are injected by reusable-owasp-check.yml.
    // Locally (no env vars set) this defaults to "never fail" (11.0, matches plugin default)
    // and an empty NVD key (slower updates, acceptable for local dev).
    failBuildOnCVSS = (System.getenv("NOVA_OWASP_FAIL_ON_CVSS") ?: "11").toFloat()
    nvd.apiKey = System.getenv("NVD_API_KEY") ?: ""
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