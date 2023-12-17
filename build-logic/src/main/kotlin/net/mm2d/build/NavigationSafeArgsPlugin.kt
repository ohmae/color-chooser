package net.mm2d.build

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class NavigationSafeArgsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.navigationSafeArgsPlugin()
    }
}

private fun Project.navigationSafeArgsPlugin() {
    with(pluginManager) {
        apply("androidx.navigation.safeargs.kotlin")
    }
}
