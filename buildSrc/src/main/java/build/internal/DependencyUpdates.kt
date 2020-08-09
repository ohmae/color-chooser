package build.internal

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.Project
import org.gradle.kotlin.dsl.named

internal fun Project.dependencyUpdatesSettings() {
    tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
        resolutionStrategy {
            rejectVersionIf {
                !isStable(candidate.version) && isStable(currentVersion)
            }
        }
    }
}

private fun isStable(version: String): Boolean {
    val hasStableKeywords = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    return hasStableKeywords || regex.matches(version)
}
