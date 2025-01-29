import kr.entree.spigradle.kotlin.paper

plugins {
    java
    kotlin("jvm")
    id("com.diffplug.spotless") version "5.8.2"
    id("kr.entree.spigradle")
}

version = "0.1.0"

dependencies {
    compileOnly(paper())
    implementation("org.jetbrains:annotations:16.0.2")
}

spotless {
    java {
        importOrder()
        prettier(
            mapOf(
                "prettier" to "2.0.5",
                "prettier-plugin-java" to "0.8.0"
            )
        ).config(
            mapOf(
                "parser" to "java",
                "tabWidth" to 4
            )
        )
        licenseHeaderFile(rootProject.file("LICENSE_HEADER"))
    }
}

tasks {
    generateSpigotDescription {
        enabled = false
    }
}
