package net.mm2d.build

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class KotlinAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugin()
    }
}

private fun Project.plugin() {
    with(pluginManager) {
        apply("org.jetbrains.kotlin.android")
    }
    android {
        kotlin {
            compilerOptions {
                jvmTarget.set(Projects.jvmTarget)
            }
        }
    }
    dependencies {
        implementation(libs.library("kotlinStdlib"))
        implementation(libs.library("kotlinxCoroutinesAndroid"))
    }
}

// DSL
private fun Project.kotlin(configure: Action<KotlinAndroidProjectExtension>): Unit =
    (this as ExtensionAware).extensions.configure("kotlin", configure)
