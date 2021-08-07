import kr.entree.spigradle.kotlin.bungeecord

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("kr.entree.spigradle") version "2.2.3"
}

dependencies {
    // jvm and kotlin dependencies
    implementation(kotlin("stdlib"))
    implementation(project(":api"))

    // server dependencies
    compileOnly(bungeecord())
}

tasks {

    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveClassifier.set("")
    }
}
