package net.mm2d.build

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class NavigationSafeArgsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugin()
    }
}

private fun Project.plugin() {
    with(pluginManager) {
        apply("androidx.navigation.safeargs.kotlin")
    }
}
