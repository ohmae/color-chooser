package net.mm2d.color.chooser.build

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class KotlinAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.android")
            }
            android {
                kotlin {
                    jvmToolchain(Projects.jdkVersion)
                }
                kotlinOptions {
                    jvmTarget = Projects.jvmTarget
                }
            }
        }
    }
}
