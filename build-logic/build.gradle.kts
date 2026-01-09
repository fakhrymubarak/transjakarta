plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.android.gradle)
    implementation(libs.ktlint.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.kover.gradle)
}
