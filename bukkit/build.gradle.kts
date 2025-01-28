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
    implementation(project(":api"))

    // server dependencies
    compileOnly(paper())
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    // Command API
    implementation("dev.jorel.CommandAPI:commandapi-shade:6.3.0")
    compileOnly("dev.jorel.CommandAPI:commandapi-core:6.3.0")

    // Postgres & Exposed
    implementation("org.jetbrains.exposed:exposed-core:0.58.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.58.0")

    // implementation("org.jetbrains.exposed", "exposed-core", "0.28.1")
    // implementation("org.jetbrains.exposed", "exposed-jdbc", "0.28.1")
    implementation("pw.forst", "exposed-upsert", "1.1.0")
    
    implementation("org.postgresql", "postgresql", "42.2.18")
    implementation("com.zaxxer", "HikariCP", "3.4.5")
}

spotless {
    kotlin {
        ktlint()
        // licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
    }
}

tasks.shadowJar {
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
