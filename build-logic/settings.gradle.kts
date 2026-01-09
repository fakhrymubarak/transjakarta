pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
        id("org.jetbrains.kotlinx.kover") version "0.8.3"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "build-logic"
