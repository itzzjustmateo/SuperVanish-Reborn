plugins {
    id("java")
    id("com.gradleup.shadow") version("9.0.0-beta4")
    id("maven-publish")
}

group = "de.devflare"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.essentialsx.net/releases")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://jitpack.io")
    maven("https://repo.citizensnpcs.co/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.mikeprimm.com/")
    maven("https://libraries.minecraft.net")
    maven("https://repo.mvdw-software.com/content/groups/public/")
}

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    // Lombok (to be removed in future)
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Plugin hooks (provided at runtime)
    compileOnly("net.essentialsx:EssentialsX:2.21.2") {
        exclude("org.bstats", "bstats-bukkit")
    }
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1") {
        exclude("org.bukkit", "bukkit")
        exclude("org.bukkit", "craftbukkit")
    }
    compileOnly("net.citizensnpcs:citizensapi:2.0.28-SNAPSHOT")
    compileOnly("com.github.SinnDevelopment:TrailGUI:37659dda03")
    compileOnly("us.dynmap:dynmap-api:3.1")
    compileOnly("be.maximvdw:MVdWPlaceholderAPI:3.1.1-SNAPSHOT") {
        exclude("org.spigotmc", "spigot")
    }
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("com.github.Jikoo:OpenInv:5.1.12")

    // Brigadier
    implementation("com.mojang:brigadier:1.0.18")

    // Shaded dependencies
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.mysql:mysql-connector-j:9.1.0")
    implementation("org.postgresql:postgresql:42.7.4")
}

tasks {
    processResources {
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand("project" to mapOf("version" to version))
        }
        filesMatching("config.yml") {
            expand("project" to mapOf("version" to version))
        }
    }

    shadowJar {
        archiveBaseName.set("SuperVanishReborn")
        archiveClassifier.set("")
        archiveVersion.set(version.toString())

        relocate("com.zaxxer.hikari", "de.devflare.svreborn.libs.hikari")
        relocate("com.mysql", "de.devflare.svreborn.libs.mysql")
        relocate("org.postgresql", "de.devflare.svreborn.libs.postgresql")

        minimize {
            exclude(dependency("com.zaxxer.hikari:HikariCP:.*"))
            exclude(dependency("com.mysql:mysql-connector-j:.*"))
            exclude(dependency("org.postgresql:postgresql:.*"))
        }

        exclude("META-INF/MANIFEST.MF")
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }

    build {
        dependsOn(shadowJar)
    }
}

artifacts {
    add("archives", tasks.shadowJar)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = rootProject.name
            version = version.toString()
            from(components["shadow"])
        }
    }
}
