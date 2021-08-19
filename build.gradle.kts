import kr.entree.spigradle.kotlin.papermc

plugins {
    java
    id("com.diffplug.spotless") version "5.8.2"
    id("kr.entree.spigradle") version "2.2.3"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "sh.foxboy"
version = "0.1.0-dev"

allprojects {
    // Declare global repositories
    repositories {
        jcenter()
        mavenCentral()

        // Add paper repository here, as it's used in both API and Bukkit modules.
        papermc()
        maven(url = "https://raw.githubusercontent.com/JorelAli/CommandAPI/mvn-repo/")
        maven(url = "https://repo.codemc.org/repository/maven-public/")
        maven(url = "https://jitpack.io" )
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

    // Spotless configuration
//    apply(plugin =  "com.diffplug.spotless")
//    spotless {
//        ratchetFrom = "origin/master"
//    }

    repositories {
        jcenter()
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        targetCompatibility = JavaVersion.VERSION_16.toString()
        sourceCompatibility = JavaVersion.VERSION_16.toString()
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
        relocate("dev.jorel", "${pkg}dev.jorel")
        relocate("org.jetbrains.exposed", "${pkg}org.jetbrains.exposed")
        relocate("org.postgresql", "${pkg}org.postgresql")
        relocate("pw.forst", "${pkg}pw.forst")
    }
}
