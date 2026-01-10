package utils

import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.DependencyHandlerScope

class CatalogDeps(
    private val handler: DependencyHandlerScope,
    private val libs: VersionCatalog,
) {
    fun implementation(vararg aliases: String) {
        aliases.forEach { alias ->
            handler.add("implementation", libs.findLibrary(alias).get())
        }
    }

    fun debugImplementation(vararg aliases: String) {
        aliases.forEach { alias ->
            handler.add("debugImplementation", libs.findLibrary(alias).get())
        }
    }

    fun testRuntimeOnly(vararg aliases: String) {
        aliases.forEach { alias ->
            handler.add("testRuntimeOnly", libs.findLibrary(alias).get())
        }
    }

    fun ksp(vararg aliases: String) {
        aliases.forEach { alias ->
            handler.add("ksp", libs.findLibrary(alias).get())
        }
    }

    fun platform(vararg aliases: String) {
        aliases.forEach { alias ->
            handler.add("implementation", handler.platform(libs.findLibrary(alias).get()))
        }
    }
}

fun DependencyHandlerScope.catalogDeps(
    libs: VersionCatalog,
    block: CatalogDeps.() -> Unit,
) {
    CatalogDeps(this, libs).block()
}
