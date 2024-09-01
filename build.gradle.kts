import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

group = "net.dungeonescape"
version = "1.0"
description = "Dungeon escape 1v1 event plugin"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.19-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    reobfJar {
        outputJar.set(layout.buildDirectory.file("libs/${rootProject.name}-${project.version}.jar"))
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    main = "net.dungeonescape.DungeonEscapeMain"
    apiVersion = "1.19"
}
