# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),  
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-06-XX

### Added

- Forked from the original SuperVanish plugin.
- Initial commit of maintained fork as **SuperVanishReborn**.

### Changed

- Refactored package names:
  - All source files moved from `de.devflare.supervanish` and `de.devflare.api.vanish` to `de.devflare.svreborn` and `de.devflare.svreborn.api`.
  - Updated all `package` declarations and `import` statements across 77+ Java files.
- Upgraded codebase for compatibility:
  - Updated Java version to 21 (see `pom.xml`).
  - Upgraded Paper API to `1.21.11-R0.1-SNAPSHOT`, with correct repository and snapshot configuration.
  - Updated dependencies for Minecraft 1.21+ (latest releases):
    - `ProtocolLib`: 5.3.0
    - `EssentialsX`: 2.21.2
    - `PlaceholderAPI`: 2.12.2
- Updated `plugin.yml`:
  - Changed `main` class path.
  - Updated `api-version` to 1.21.

### Removed

- Deep cleaned the repository by deleting extraneous/old files:
  - Build artifacts (`target/`, `dependency-reduced-pom.xml`)
  - Build logs (`build.log`, `build_errors.log`, `mvn_error.txt`)
  - IDE metadata (`.idea/`, `.vscode/`, `.settings/`, `.classpath`, `.project`, `*.iml`)
  - Temporary/OS files (`bin/`, `Thumbs.db`, `.polyglot.pom.yml`)

### Fixed

- Resolved compilation errors in `VersionUtil.java` by correcting illegal escape characters in version splitting logic.

### Security

- Verified all Java files for correct, safe package declarations without syntax or escaping artifacts.

### Misc

- `mvn clean install` now builds and compiles successfully with Java 21 (tests skipped for verification).

[Unreleased]: https://github.com/itzzjustmateo/SuperVanishReborn/compare/v1.0.0...HEAD
