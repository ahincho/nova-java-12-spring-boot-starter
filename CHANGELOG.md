# Changelog

## [3.1.0](https://github.com/ahincho/nova-java-12-spring-boot-starter/compare/v3.0.0...v3.1.0) (2026-10-02)


### Features

* bring the CQRS starter 1.0.0 ([7853de2](https://github.com/ahincho/nova-java-12-spring-boot-starter/commit/7853de27aad0477d8a7bc80de311c17938c20eff))

## [3.0.0](https://github.com/ahincho/nova-java-12-spring-boot-starter/compare/v2.0.0...v3.0.0) (2026-10-02)


### ⚠ BREAKING CHANGES

* a service that uses the meta-starter no longer gets a field masked just for being called name, email, phone, dni, card, account, ip or one of the other names on the inference list. Mark the fields that are personal data with @Masked or @MaskedClass, or set nova.mask.infer-by-field-name=true to keep the previous behavior.

### Features

* take the commons starters 4.0.0, which mask only what is annotated ([c1a0f34](https://github.com/ahincho/nova-java-12-spring-boot-starter/commit/c1a0f34f83964da37a71f269ad9161986bfdf09c))


### Bug Fixes

* register the startup validation with the Spring Boot 4 key and a deferred log ([600f2f6](https://github.com/ahincho/nova-java-12-spring-boot-starter/commit/600f2f68672fdc47c129ad21c359b04e2ba2afe8))


### Documentation

* describe what the annotation, the auto-configuration and the post-processor do ([76a356c](https://github.com/ahincho/nova-java-12-spring-boot-starter/commit/76a356cf2daa143f87421cb47967f9880fe7a3b7))

## [2.0.0](https://github.com/ahincho/nova-java-12-spring-boot-starter/compare/v1.0.4...v2.0.0) (2026-10-02)


### ⚠ BREAKING CHANGES

* a service that used the meta-starter without nova-spring-boot-bom 3.x now receives the API standard and mask starters 3.0.1, with the layered errors of ADR-031, plus observability 3.0.0 and secrets 1.2.0.

### Features

* declare the starter versions and bring observability and secrets ([f4c3ef6](https://github.com/ahincho/nova-java-12-spring-boot-starter/commit/f4c3ef6db786732d86bf8f2b294f9ad8fa05c0ca))


### Bug Fixes

* **deps:** pin kotlin-stdlib 2.4.0 for CVE-2026-53914 ([8853e1b](https://github.com/ahincho/nova-java-12-spring-boot-starter/commit/8853e1b56dd7b5f6cad01c09c852aed4e6f66633))

## [1.0.4](https://github.com/ahincho/nova-java-12-spring-boot-starter/compare/v1.0.3...v1.0.4) (2026-09-27)


### Bug Fixes

* **deps:** depend on the renamed starters through nova-spring-boot-bom 2.0.0 ([c20e720](https://github.com/ahincho/nova-java-12-spring-boot-starter/commit/c20e720140942076d74c57797bf182b323bc6d6c))
* **deps:** depend on the renamed starters through nova-spring-boot-bom 2.0.0 ([d4f6758](https://github.com/ahincho/nova-java-12-spring-boot-starter/commit/d4f6758d9a6d344bc29cea87b32119188a501826))

## [1.0.3](https://github.com/ahincho/nova-java-12-spring-boot-starter/compare/v1.0.2...v1.0.3) (2026-09-27)


### Documentation

* add a README and adopt EPL-2.0 ([d16917d](https://github.com/ahincho/nova-java-12-spring-boot-starter/commit/d16917df4e61d06655d84a218cd4d67752def762))

## [1.0.2](https://github.com/ahincho/nova-java-spring-boot-starter/compare/v1.0.1...v1.0.2) (2026-07-13)


### Bug Fixes

* **ci:** add component + skip-snapshot + manifest-file (mask-utils pattern) ([fc14410](https://github.com/ahincho/nova-java-spring-boot-starter/commit/fc14410086c0b8206fc5258e4025e511b57ea9f8))
* **ci:** add last-release-sha, include-component-in-tag: false, release-type: java to top-level config; pass manifest-file in wrapper ([055b4f3](https://github.com/ahincho/nova-java-spring-boot-starter/commit/055b4f3e94ed47cdfdcbb0a97cc560fc9075bc45))

## [1.0.1](https://github.com/ahincho/nova-java-spring-boot-starter/compare/v1.0.0...v1.0.1) (2026-07-13)


### Bug Fixes

* **ci:** add component + skip-snapshot + manifest-file (mask-utils pattern) ([fc14410](https://github.com/ahincho/nova-java-spring-boot-starter/commit/fc14410086c0b8206fc5258e4025e511b57ea9f8))
* **ci:** add last-release-sha, include-component-in-tag: false, release-type: java to top-level config; pass manifest-file in wrapper ([055b4f3](https://github.com/ahincho/nova-java-spring-boot-starter/commit/055b4f3e94ed47cdfdcbb0a97cc560fc9075bc45))
* **docs:** bump [@version](https://github.com/version) Javadoc to 1.0.1 (NOVA-SEMVER-22 follow-up) ([a22453f](https://github.com/ahincho/nova-java-spring-boot-starter/commit/a22453f95cd926f726803b0c5d31a023b32181da))

## 1.0.0 (2026-07-10)


### Features

* **ci:** migrate to release-please + tag-based publish flow (NOVA-SEMVER-13) ([eae7ffa](https://github.com/ahincho/nova-java-spring-boot-starter/commit/eae7ffa73b08b4b4ea7785bde57207739a70cbc1))
* **gradle:** add GPG signing plugin for Maven Central publishing (NOVA-SEMVER-10) ([fac1e63](https://github.com/ahincho/nova-java-spring-boot-starter/commit/fac1e63757cb99bd5a51e49ad5218abb1796db1b))
* **gradle:** enable Local Build Cache and Configuration Cache (NOVA-SEMVER-23-24) ([9fca7f6](https://github.com/ahincho/nova-java-spring-boot-starter/commit/9fca7f6be37581f2752b4fcab2daaa37cb603b01))
* initial commit - Meta-framework starter: @NovaSpringBootApplication annotation, environment post-processor ([356ca53](https://github.com/ahincho/nova-java-spring-boot-starter/commit/356ca537ccc04e7440964fb12bcf8ee1a35d9d4c))


### Bug Fixes

* **ci:** inline publish-on-tag and remove dirty closure for Gradle 9.6.1 ([6f1ba0c](https://github.com/ahincho/nova-java-spring-boot-starter/commit/6f1ba0cf3b1b8b9a49cb093f96a1296b72c96f11))
* **ci:** use PAT fallback for release-please to enable tag-triggered workflows ([69d42db](https://github.com/ahincho/nova-java-spring-boot-starter/commit/69d42dbc8ea4dcdff8ad0ea52f2308a08f36d348))
