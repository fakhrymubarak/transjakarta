import utils.catalogDeps

plugins {
    id("transjakarta.ktlint")
    id("transjakarta.kover")
}

private val libs: VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    catalogDeps(libs) {
        testRuntimeOnly(
            LibraryAliases.JUNIT_JUPITER_ENGINE,
            LibraryAliases.JUNIT_PLATFORM_LAUNCHER,
        )
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
