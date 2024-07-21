package net.mm2d.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.DokkaTaskPartial

class DocumentationDokkaPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugin()
    }
}

private fun Project.plugin() {
    with(pluginManager) {
        apply("org.jetbrains.dokka")
    }
    dependencies {
        dokkaPlugin(libs.library("dokkaAndroidDocumentationPlugin"))
    }
    tasks.dokkaHtml {
        moduleName.set(base.archivesName.get())
    }
    tasks.dokkaHtmlPartial {
        moduleName.set(base.archivesName.get())
    }
    tasks.dokkaJavadoc {
        moduleName.set(base.archivesName.get())
    }
}

// DSL
private val TaskContainer.dokkaHtml: TaskProvider<DokkaTask>
    get() = named<DokkaTask>("dokkaHtml")

private val TaskContainer.dokkaHtmlPartial: TaskProvider<DokkaTaskPartial>
    get() = named<DokkaTaskPartial>("dokkaHtmlPartial")

private val TaskContainer.dokkaJavadoc: TaskProvider<DokkaTask>
    get() = named<DokkaTask>("dokkaJavadoc")

private fun DependencyHandlerScope.dokkaPlugin(artifact: MinimalExternalModuleDependency) {
    add("dokkaPlugin", artifact)
}
