# Roadmap

This document outlines the planned major improvements for **SuperVanish Reborn**, a maintained fork of the original SuperVanish plugin.

---

## #1 Rewrite in Gradle (High Priority)

Migrate from Maven to Gradle (Kotlin DSL) for a more modern, flexible, and maintainable build system. This will:

- Simplify dependency management
- Enable better multi-project support if needed
- Improve build performance with incremental builds and build caching
- Make it easier to contribute with a more intuitive DSL
- Use Gradle's version catalog for centralized dependency management
- Enable GitHub Actions CI with Gradle caching

---

## #2 Add Minecraft 26.1 Support

Update the plugin to support Minecraft 26.1, ensuring compatibility with the latest server software. This includes:

- Updating Paper API and mappings to 26.1
- Adapting to any API changes or deprecations
- Testing core functionality on 26.1 servers
- Maintaining backwards compatibility where possible
- Dropping support for legacy Minecraft versions to reduce maintenance burden

---

## #3 Remove Lombok Dependency

Remove the Lombok dependency from the project to fix build reproducibility issues and reduce tooling friction. All generated code (getters, setters, builders, etc.) will be written out explicitly. This improves:

- Build reliability across different IDEs and environments
- Code transparency and readability
- Ease of contribution (no Lombok plugin required)
- Compatibility with modern Java tooling

---

## #4 Code Improvements & Best Practices

Modernize the entire codebase with a focus on quality, maintainability, and performance:

- Refactor legacy code patterns to use modern Java/Paper APIs
- Adopt consistent code style and project conventions
- Improve error handling and logging
- Add comprehensive documentation (JavaDoc) for public APIs
- Write unit and integration tests (JUnit 5 + MockBukkit)
- Reduce technical debt and simplify complex logic
- Follow security best practices
- Enable modern Java features (records, sealed classes, pattern matching, etc.)

---

## #5 Plugin Hooks & Integrations

Improve and expand third-party plugin integrations:

- Update EssentialsX hook for latest API
- Add support for more permission plugins
- Improve PlaceholderAPI expansion with new placeholders
- Add optional grief prevention integration
- Improve ProtocolLib usage for packet-level vanish
- Maintain hooks for Citizens, Dynmap, MVdWPlaceholderAPI, TrailGUI, OpenInv

---

## #6 Database & Storage Enhancements

Replace the current storage layer with a more robust system:

- Add SQLite support alongside existing MySQL and PostgreSQL
- Improve database schema with migrations support
- Add per-player persistent vanish state across restarts
- Add configuration GUI for in-game management
- Implement caching layer for better performance

---

## #7 Vanish Features & Quality of Life

Add new vanish-related features requested by the community:

- Vanish on join (auto-vanish when joining the server)
- Sound effects on vanish/unvanish (configurable)
- Per-player vanish lists (staff can choose who can see them)
- Better visual indicators for vanished players
- Vanished player tracking for staff
- Configurable vanish messages with MiniMessage formatting
- Improved tab list handling

---

## #8 Developer API

Provide a stable and documented API for other plugins to integrate:

- Public API module with interface definitions
- Event system for vanish/unvanish events
- Ability to check player vanish state via API
- JavaDoc and example usage documentation
- API versioning for stability

---

## #9 CI/CD & Project Infrastructure

Set up modern development infrastructure:

- GitHub Actions for automated builds and tests
- Automated release workflow on tag push
- Code quality checks (SpotBugs, Checkstyle, PMD)
- Dependency scanning with Dependabot
- Automated deployment to Modrinth/Hangar/SpigotMC
- Code coverage reporting

---

## #10 Multi-Language / Internationalization

Make the plugin accessible to a global audience:

- Externalize all user-facing strings
- Add language file system (en.yml, de.yml, etc.)
- Community-contributed translations
- Fallback to default language for missing translations
- MiniMessage support across all languages
