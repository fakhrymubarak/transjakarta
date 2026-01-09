import com.android.build.gradle.LibraryExtension

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("transjakarta.ktlint")
    id("transjakarta.kover")
}

extensions.configure<LibraryExtension> {
    val baseNamespace = "com.fakhry.transjakarta"
    val modulePath = project.path.split(":").filter { it.isNotBlank() }.joinToString(".")
    namespace = if (modulePath.isBlank()) baseNamespace else "$baseNamespace.$modulePath"

    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}
