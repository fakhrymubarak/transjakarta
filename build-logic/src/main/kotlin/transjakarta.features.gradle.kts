import com.android.build.gradle.LibraryExtension
import dagger.hilt.android.plugin.HiltExtension
import utils.catalogDeps

plugins {
    id("transjakarta.library")
    id("org.jetbrains.kotlin.plugin.serialization")
}

private val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(project(":core:networking"))
    implementation(project(":core:domain"))
    implementation(project(":core:designsystem"))

    catalogDeps(libs) {
        platform(LibraryAliases.COMPOSE_BOM)
        implementation(
            // Compose
            LibraryAliases.COMPOSE_MATERIAL3,
            LibraryAliases.COMPOSE_UI,
            LibraryAliases.COMPOSE_UI_GRAPHICS,
            LibraryAliases.COMPOSE_UI_TOOLING_PREVIEW,
            LibraryAliases.COMPOSE_MATERIAL_ICONS_EXTENDED,
            LibraryAliases.COMPOSE_SHIMMER,

            // Hilt
            LibraryAliases.ANDROIDX_LIFECYCLE_RUNTIME_KTX,
            LibraryAliases.ANDROIDX_LIFECYCLE_VIEWMODEL_COMPOSE,
            LibraryAliases.HILT_ANDROID,
            LibraryAliases.HILT_NAVIGATION_COMPOSE,

            // Collection Immutable
            LibraryAliases.KOTLINX_COLLECTIONS_IMMUTABLE
        )
        debugImplementation(LibraryAliases.COMPOSE_UI_TOOLING)
    }
}

extensions.configure<LibraryExtension> {
    buildFeatures {
        compose = true
    }
}

pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
pluginManager.apply("com.google.devtools.ksp")
pluginManager.apply("com.google.dagger.hilt.android")

pluginManager.withPlugin("com.google.devtools.ksp") {
    dependencies {
        catalogDeps(libs) {
            ksp(LibraryAliases.HILT_COMPILER)
        }
    }
}

pluginManager.withPlugin("com.google.dagger.hilt.android") {
    extensions.configure<HiltExtension> {
        enableAggregatingTask = false
    }
}
