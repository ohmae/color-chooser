package net.mm2d.build

import com.dropbox.gradle.plugins.dependencyguard.DependencyGuardPluginExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

class DependencyGuardPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugin()
    }
}

private fun Project.plugin() {
    with(pluginManager) {
        apply("com.dropbox.dependency-guard")
    }
    dependencyGuard {
        configuration("releaseRuntimeClasspath")
    }
}

// DSL
fun Project.dependencyGuard(configure: Action<DependencyGuardPluginExtension>): Unit =
    (this as ExtensionAware).extensions.configure("dependencyGuard", configure)
