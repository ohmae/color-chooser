package net.mm2d.build

import com.android.build.gradle.TestedExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

@Suppress("unused")
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
            jvmToolchain(Projects.jdkVersion)
        }
        kotlinOptions {
            jvmTarget = Projects.jvmTarget
        }
    }
    dependencies {
        implementation(libs.library("kotlinStdlib"))
        implementation(libs.library("kotlinxCoroutinesAndroid"))
    }
}

// DSL
private fun Project.kotlin(action: KotlinAndroidProjectExtension.() -> Unit): Unit =
    extensions.configure(action)

private fun TestedExtension.kotlinOptions(block: KotlinJvmOptions.() -> Unit): Unit =
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
