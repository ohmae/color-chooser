package net.mm2d.color.chooser.build

import com.android.build.gradle.TestedExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

internal fun Project.android(action: TestedExtension.() -> Unit): Unit =
    extensions.configure(action)

internal val Project.base: BasePluginExtension
    get() = (this as ExtensionAware).extensions.getByName("base") as BasePluginExtension

internal fun DependencyHandlerScope.implementation(artifact: MinimalExternalModuleDependency) {
    add("implementation", artifact)
}

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.library(name: String): MinimalExternalModuleDependency {
    return findLibrary(name).get().get()
}
