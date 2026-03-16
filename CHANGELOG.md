# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),  
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Todo]

### Added

- **Added:** MySQL/MariaDB/PostgreSQL synchronization for real-time vanish state syncing across multiple servers via a shared database table (`sv_vanished_players`). Configure under the new `database:` section in `config.yml`. Disabled by default; falls back to file-based storage if the connection fails.
- **Planned:** Native Velocity proxy support for better cross-server invisibility handling.
- **Planned:** Modernized Developer API for better integration with 3rd-party plugins.
- **Planned:** Customizable particle and sound effects using MiniMessage templates.
- **Planned:** Persistent vanish states that survive server restarts and crashes.
- **Planned:** New `/sv gui` for staff to manage vanish states and settings visually.
- **Planned:** Integration with LuckPerms to hide vanished status in `/lp user info`.

### Changed

- _(Future)_ Refactor persistence layer for better reliability on large servers.

### Fixed

- _(Future)_ Minor edge cases in tab-completion visibility on 1.21.x.

## [1.0.0] - 2026-02-??

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
- Fixed compilation errors in `ActionBarMgr.java` and `VanishIndication.java` regarding unreachable `InvocationTargetException` catch blocks due to ProtocolLib 5.x API changes.
- Modernized messaging system by switching to **Adventure API** and **MiniMessage**.
- Replaced legacy `&` color codes with MiniMessage tags in `messages.yml`.
- Resolved deprecation of `spigot().sendMessage` and `fromLegacyText`.
- Fixed corrupted copyright header encoding across all source files.

### Security

- Verified all Java files for correct, safe package declarations without syntax or escaping artifacts.

### Misc

- `mvn clean install` now builds and compiles successfully with Java 21 (tests skipped for verification).

[Unreleased]: https://github.com/itzzjustmateo/SuperVanishReborn/compare/v1.0.0...HEAD
