import java.util.Properties

plugins {
    alias(libs.plugins.transjakarta.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val localProperties =
            Properties().apply {
                val localPropertiesFile = rootProject.file("local.properties")
                if (localPropertiesFile.exists()) {
                    localPropertiesFile.inputStream().use { load(it) }
                }
            }
        val mbtaApiKey = localProperties.getProperty("MBTA_API_KEY") ?: ""
        buildConfigField("String", "MBTA_API_KEY", "\"$mbtaApiKey\"")
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
