# SuperVanish Reborn

**SuperVanish Reborn** is an updated and maintained fork of [SuperVanish](https://www.spigotmc.org/resources/supervanish-be-invisible.1331/), a popular Bukkit plugin for Paper Minecraft servers. This plugin allows server admins to become completely invisible and undetectable to regular players, greatly assisting with administrative work and moderation. This fork adds many improvements, modernizations, and fixes on top of the original.

---

## Features

- Seamless vanish/invisibility for server staff
- Undetectable by other players and most plugins
- Simple, reliable, and highly configurable
- Modern codebase and updated for recent Minecraft versions (Java 21 support)
- Modernized configuration system (`snake_case` keys)
- Advanced text formatting using MiniMessage & Adventure API
- Enhanced placeholder system with more descriptive names
- Actively maintained fork with community-driven improvements

---

## Getting Started

### Prerequisites

- Minecraft server running Paper, or Purpur
- Java 21 or newer

### Installation

1. Download the latest release from the [releases page](https://github.com/itzzjustmateo/SuperVanishReborn/releases) or build it yourself (see below).
2. Place the `.jar` file in your server's `plugins/` directory.
3. Restart your server.
4. Configure via `plugins/SuperVanish-Reborn/config.yml` as needed. All keys are now in `snake_case` for better readability.
5. Customize your messages in `messages.yml`, now supporting MiniMessage tags and modernized placeholders (e.g., `%player%`, `%target%`, `%display_name%`).

---

## Building from Source

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.YOUR-GITHUB-USERNAME</groupId>
        <artifactId>SuperVanishReborn</artifactId>
        <version>LATEST_VERSION</version>
    </dependency>
</dependencies>
```

Replace `YOUR-GITHUB-USERNAME` and `LATEST_VERSION` with the appropriate values if using JitPack to build directly from this fork.

---

## Contribution & Support

Pull requests are welcome! Please keep them as small, self-contained, and well-tested as possible for easier review. If you find issues or have suggestions, please open an issue on [GitHub](https://github.com/itzzjustmateo/SuperVanishReborn/issues).

---

## Credits

Original SuperVanish by [LeonMangler](https://www.spigotmc.org/resources/supervanish-be-invisible.1331/).  
SuperVanish Reborn is a community-driven fork maintained by [DevFlare](https://www.devflare.de) and contributors. This project builds upon the original work with modernizations, bug fixes, and new features.

---

## Roadmap

See [ROADMAP.md](./ROADMAP.md) for planned improvements and fixes.

---

## License

This project is licensed under the **Apache License, Version 2.0**.  
The original SuperVanish code by Leon Mangler is licensed under the **MIT License**.  
See [LICENSE](./LICENSE) for full details.

---

Enjoy vanishing!
