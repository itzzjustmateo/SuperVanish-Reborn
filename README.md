# SuperVanishReborn

**SuperVanishReborn** is a maintained fork of [SuperVanish](https://www.spigotmc.org/resources/supervanish-be-invisible.1331/), a popular Bukkit plugin for Spigot/CraftBukkit Minecraft servers. This plugin allows server admins to become completely invisible and undetectable to regular players, greatly assisting with administrative work and moderation.

---

## Features

- Seamless vanish/invisibility for server staff
- Undetectable by other players and most plugins
- Simple, reliable, and highly configurable
- Modern codebase and updated for recent Minecraft versions (Java 21 support)
- Actively maintained fork with community-driven improvements

---

## Getting Started

### Prerequisites

- Minecraft server running Spigot, Paper, or CraftBukkit
- Java 21 or newer

### Installation

1. Download the latest release from the [releases page](https://github.com/itzzjustmateo/SuperVanishReborn/releases) or build it yourself (see below).
2. Place the `.jar` file in your server's `plugins/` directory.
3. Restart your server.
4. Configure via `plugins/SuperVanish-Reborn/config.yml` as needed.

---

## Building from Source (Maven)

To include **SuperVanishReborn** in your Maven project:

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
SuperVanishReborn maintained by [DevFlare](https://www.devflare.de) and the open-source community.

Enjoy vanishing!
