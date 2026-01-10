plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

// Plugin Dependencies using Libs on Version Catalog
dependencies {
    implementation(libs.android.gradle)
    implementation(libs.hilt.gradle)
    implementation(libs.ksp.gradle)
    implementation(libs.ktlint.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.kover.gradle)
    implementation(libs.compose.gradle)
    implementation(libs.serialization.gradle)
}
