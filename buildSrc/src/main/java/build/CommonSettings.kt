package build

import build.internal.*
import org.gradle.api.Project

fun Project.commonSettings() {
    sourcesJarSettings()
    uploadArchivesSettings()
    publishingSettings()
    bintraySettings()
    dependencyUpdatesSettings()
}

fun Project.dependencyUpdates() {
    dependencyUpdatesSettings()
}
