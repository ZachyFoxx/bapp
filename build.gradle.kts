import kr.entree.spigradle.kotlin.papermc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.10"
    id("com.diffplug.spotless") version "5.8.2"
    id("kr.entree.spigradle") version "2.2.3"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "sh.foxboy"
version = "1.0.0"

allprojects {
    // Declare global repositories
    repositories {
        jcenter()
        mavenCentral()

        // Add paper repository here, as it's used in both API and Bukkit modules.
        papermc()
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":bukkit"))
}

subprojects {
    group = "sh.foxboy.bapp"

    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "kr.entree.spigradle")

    // Spotless configuration
    apply(plugin =  "com.diffplug.spotless")
    spotless {
        ratchetFrom = "origin/master"
    }

    repositories {
        jcenter()
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        targetCompatibility = JavaVersion.VERSION_11.toString()
        sourceCompatibility = JavaVersion.VERSION_11.toString()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

tasks {
    // Disable root project building spigot description.
    generateSpigotDescription {
        enabled = false
    }
    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveClassifier.set("")
        val pkg = "sh.foxboy.bapp.libs."
        relocate("com.zaxxer", "${pkg}com.zaxxer")
        relocate("org.postgresql", "${pkg}org.postgresql")
    }
}
