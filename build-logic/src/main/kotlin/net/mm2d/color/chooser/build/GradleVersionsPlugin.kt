package net.mm2d.color.chooser.build

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.named

@Suppress("unused")
class GradleVersionsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.gradleVersionsPlugin()
    }
}

private fun Project.gradleVersionsPlugin() {
    with(pluginManager) {
        apply("com.github.ben-manes.versions")
    }
    tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
        rejectVersionIf { !isStable(candidate.version) }
    }
}

private fun isStable(version: String): Boolean {
    val versionUpperCase = version.uppercase()
    val hasStableKeyword =
        listOf("RELEASE", "FINAL", "GA").any { versionUpperCase.contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    return hasStableKeyword || regex.matches(version)
}
