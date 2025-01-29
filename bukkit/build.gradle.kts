import kr.entree.spigradle.data.Load
import kr.entree.spigradle.kotlin.*

plugins {
    kotlin("jvm")
    id("com.diffplug.spotless") version "5.8.2"
    id("kr.entree.spigradle")
    id("com.gradleup.shadow")
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://raw.githubusercontent.com/JorelAli/CommandAPI/mvn-repo/") }
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }

}

dependencies {
    // jvm and kotlin dependencies
    implementation(kotlin("stdlib"))
    compileOnly(project(":api"))

    // server dependencies
    compileOnly(paper())
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    // Command API
    implementation("dev.jorel:commandapi-bukkit-shade:9.7.0")

    // Postgres & Exposed
    implementation("org.jetbrains.exposed:exposed-core:0.58.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.58.0")
    implementation("pw.forst", "exposed-upsert", "1.1.0")
    
    implementation("org.postgresql", "postgresql", "42.7.5")
    implementation("com.zaxxer", "HikariCP", "6.2.1")
}

spotless {
    kotlin {
        ktlint()
        // licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
    }
}

tasks.shadowJar {
    minimize()
    archiveClassifier.set("")
}

spigot {
    name = "Bapp"
    authors = mutableListOf("ZachyFoxx")
    apiVersion = "1.16"
    softDepends = mutableListOf("LuckPerms")
    depends = listOf("Vault")
    version = "0.1.0-dev"
}

tasks.named("build") {
    dependsOn("shadowJar")
}
