package net.mm2d.color.chooser.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import java.io.File

@Suppress("unused")
class DocumentationDokkaPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.dokka")
            }
            dependencies {
                dokkaPlugin(libs.library("dokkaAndroidDocumentationPlugin"))
            }
            tasks.dokkaHtml.configure {
                outputDirectory.set(File(projectDir, "../docs/dokka"))
                moduleName.set(base.archivesName.get())
            }
            tasks.dokkaJavadoc.configure {
                outputDirectory.set(File(layout.buildDirectory.asFile.get(), "docs/javadoc"))
                moduleName.set(base.archivesName.get())
            }
        }
    }
}
