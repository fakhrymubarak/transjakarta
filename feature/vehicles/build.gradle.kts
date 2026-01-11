plugins {
    alias(libs.plugins.transjakarta.features)
}

dependencies {
    implementation(project(":core:utils"))

    // Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // Testing
    testImplementation(libs.junit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.kotlin)
}
