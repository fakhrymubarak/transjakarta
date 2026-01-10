plugins {
    id("org.jetbrains.kotlinx.kover")
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "**/R",
                    "**/R$*",
                    "**/BuildConfig",
                    "**/Manifest*",
                    "*Screen",
                    "*ScreenKt",
                    "*_*Factory*",
                    "*_Factory*",
                    "Hilt_*",
                    "*_Hilt*",
                    "*ComposableSingletons*",
                )
                annotatedBy(
                    "androidx.compose.ui.tooling.preview.Preview",
                    "androidx.compose.runtime.Composable",
                    "dagger.Module",
                    "kotlinx.serialization.Serializable"
                )
                packages(
                    "*model*",
                    "*response*",
                    "*hilt_aggregated_deps*",
                )
            }
        }
        verify {
            rule {
                disabled = !project.path.startsWith(":feature:")
                minBound(90)
            }
        }
    }
}
