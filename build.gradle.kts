import kr.entree.spigradle.data.Load
import kr.entree.spigradle.kotlin.*

plugins {
    java
    kotlin("jvm") version "2.0.21"
    id("com.diffplug.spotless") version "5.8.2"
    id("kr.entree.spigradle") version "2.4.4"
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

group = "sh.foxboy"
version = "0.1.0-dev"

allprojects {
    // Declare global repositories
    repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://raw.githubusercontent.com/JorelAli/CommandAPI/mvn-repo/") }
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }

    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":bukkit"))
}

subprojects {
    group = "sh.foxboy.bapp"

    apply(plugin = "java")
    apply(plugin = "kr.entree.spigradle")
    apply(plugin = "com.diffplug.spotless")

    // Spotless configuration
    apply(plugin =  "com.diffplug.spotless")
    spotless {
        ratchetFrom = "origin/master"
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        targetCompatibility = JavaVersion.VERSION_21.toString()
        sourceCompatibility = JavaVersion.VERSION_21.toString()
    }

}

tasks.shadowJar {
    archiveClassifier.set("")
    val pkg = "sh.foxboy.bapp.libs."
    relocate("com.zaxxer", "${pkg}com.zaxxer")
    relocate("org.postgresql", "${pkg}org.postgresql")
    relocate("dev.jorel", "${pkg}dev.jorel")
    relocate("org.jetbrains.exposed", "${pkg}org.jetbrains.exposed")
    relocate("org.postgresql", "${pkg}org.postgresql")
    relocate("pw.forst", "${pkg}pw.forst")
}
tasks.generateSpigotDescription {
    enabled = false
}

tasks.named("build") {
    dependsOn("shadowJar")
}
