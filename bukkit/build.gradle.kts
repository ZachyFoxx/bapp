import kr.entree.spigradle.kotlin.paper
import kr.entree.spigradle.kotlin.papermc

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("kr.entree.spigradle")
}

repositories {
    papermc()
}

dependencies {
    // jvm and kotlin dependencies
    implementation(kotlin("stdlib"))
    implementation(project(":api"))

    // server dependencies
    compileOnly(paper())
    implementation("com.github.MilkBowl:VaultAPI:1.7")

    // Command API
    implementation("dev.jorel.CommandAPI:commandapi-shade:6.3.0")

    // Postgres & Exposed
    implementation("org.jetbrains.exposed", "exposed-core", "0.28.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.28.1")
    implementation("pw.forst", "exposed-upsert", "1.0")
    implementation("org.postgresql", "postgresql", "42.2.18")
    implementation("com.zaxxer", "HikariCP", "3.4.5")
}

spotless {
    kotlin {
        ktlint()
        licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
    }
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveClassifier.set("")
    }

    spigot {
        name = "Bapp"
        authors = mutableListOf("ZachyFoxx")
        apiVersion = "1.16"
        softDepends = mutableListOf()
        depends = listOf("vault")
    }
}
