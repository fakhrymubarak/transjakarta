plugins {
    alias(libs.plugins.transjakarta.library)
    alias(libs.plugins.secrets.gradle.plugin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
    }
}

hilt {
    enableAggregatingTask = false
}

dependencies {
    implementation(project(":core:domain"))

    // Serialization
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)

    // Retrofit and Okhttp
    api(libs.retrofit.core)
    api(libs.okhttp.core)
    api(libs.okhttp.logging)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
}

secrets {
    propertiesFileName = "secret.properties"
    defaultPropertiesFileName = "default.properties"
}
