plugins {
    alias(libs.plugins.transjakarta.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
}

dependencies {
    implementation(libs.androidx.annotation.jvm)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.kotlinx.coroutines.android)
}
