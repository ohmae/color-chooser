package net.mm2d.build

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugin()
    }
}

private fun Project.plugin() {
    with(pluginManager) {
        apply("com.android.application")
    }
    androidApplication {
        android {
            compileSdk = Projects.compileSdk

            defaultConfig {
                minSdk = Projects.minSdk
                targetSdk = Projects.targetSdk
            }
            compileOptions {
                sourceCompatibility = Projects.sourceCompatibility
                targetCompatibility = Projects.targetCompatibility
            }
            buildFeatures {
                viewBinding = true
            }
            lint {
                abortOnError = true
            }
            testOptions {
                unitTests.isIncludeAndroidResources = true
            }
        }
    }
}

// DSL
private fun Project.androidApplication(action: BaseAppModuleExtension.() -> Unit): Unit =
    extensions.configure(action)
