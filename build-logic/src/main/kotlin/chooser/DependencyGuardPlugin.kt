package chooser

import com.dropbox.gradle.plugins.dependencyguard.DependencyGuardPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class DependencyGuardPlugin : Plugin<Project> {
    override fun apply(
        target: Project,
    ): Unit =
        with(target) {
            pluginManager.apply("com.dropbox.dependency-guard")
            extensions.configure<DependencyGuardPluginExtension> {
                configuration("releaseRuntimeClasspath")
            }
        }
}
