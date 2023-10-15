package net.mm2d.color.chooser.build

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

internal fun Project.androidApplication(action: BaseAppModuleExtension.() -> Unit): Unit =
    extensions.configure(action)

internal fun Project.androidLibrary(action: LibraryExtension.() -> Unit): Unit =
    extensions.configure(action)

internal fun Project.android(action: TestedExtension.() -> Unit): Unit =
    extensions.configure(action)

internal fun Project.kotlin(action: KotlinAndroidProjectExtension.() -> Unit): Unit =
    extensions.configure(action)

internal fun TestedExtension.kotlinOptions(block: KotlinJvmOptions.() -> Unit): Unit =
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)

val Project.base: BasePluginExtension
    get() = (this as ExtensionAware).extensions.getByName("base") as BasePluginExtension

val Project.android: LibraryExtension
    get() =
        (this as ExtensionAware).extensions.getByName("android") as LibraryExtension

val Project.publishing: PublishingExtension
    get() = (this as ExtensionAware).extensions.getByName("publishing") as PublishingExtension

fun Project.publishing(configure: Action<PublishingExtension>): Unit =
    (this as ExtensionAware).extensions.configure("publishing", configure)

fun Project.`signing`(configure: Action<SigningExtension>): Unit =
    (this as ExtensionAware).extensions.configure("signing", configure)

internal val TaskContainer.dokkaHtml: TaskProvider<DokkaTask>
    get() = named<DokkaTask>("dokkaHtml")

internal val TaskContainer.dokkaJavadoc: TaskProvider<DokkaTask>
    get() = named<DokkaTask>("dokkaJavadoc")

internal fun DependencyHandlerScope.implementation(artifact: Dependency) {
    add("implementation", artifact)
}

internal fun DependencyHandlerScope.dokkaPlugin(artifact: MinimalExternalModuleDependency) {
    add("dokkaPlugin", artifact)
}

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.library(name: String): MinimalExternalModuleDependency {
    return findLibrary(name).get().get()
}
