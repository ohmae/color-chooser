package net.mm2d.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named
import org.jetbrains.dokka.gradle.DokkaTask
import java.io.File

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
    tasks.dokkaHtml.configure {
        moduleName.set(base.archivesName.get())
        outputDirectory.set(File(layout.buildDirectory.asFile.get(), "docs/html"))
    }
    tasks.dokkaJavadoc.configure {
        moduleName.set(base.archivesName.get())
        outputDirectory.set(File(layout.buildDirectory.asFile.get(), "docs/javadoc"))
    }
}

// DSL
private val TaskContainer.dokkaHtml: TaskProvider<DokkaTask>
    get() = named<DokkaTask>("dokkaHtml")

private val TaskContainer.dokkaJavadoc: TaskProvider<DokkaTask>
    get() = named<DokkaTask>("dokkaJavadoc")

private fun DependencyHandlerScope.dokkaPlugin(artifact: MinimalExternalModuleDependency) {
    add("dokkaPlugin", artifact)
}
