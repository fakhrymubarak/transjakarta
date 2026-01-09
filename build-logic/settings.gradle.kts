pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.library") version "8.13.2"
        id("org.jetbrains.kotlin.android") version "2.3.0"
        id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
        id("org.jetbrains.kotlinx.kover") version "0.9.4"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
