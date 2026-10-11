package chooser

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.dokka.gradle.DokkaExtension

class DocumentationDokkaPlugin : Plugin<Project> {
    override fun apply(
        target: Project,
    ): Unit =
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.dokka")
                apply("org.jetbrains.dokka-javadoc")
            }
            dependencies {
                add("dokkaPlugin", libs.library("dokkaAndroidDocumentationPlugin"))
            }
            extensions.configure<DokkaExtension> {
                moduleName.set(project.extensions.getByType<BasePluginExtension>().archivesName.get())
            }
        }
}
