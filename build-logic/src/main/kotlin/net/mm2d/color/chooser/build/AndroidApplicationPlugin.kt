package net.mm2d.color.chooser.build

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }
            androidApplication {
                android {
                    compileSdk = Config.compileSdk

                    defaultConfig {
                        minSdk = Config.minSdk
                        targetSdk = Config.targetSdk
                    }
                    compileOptions {
                        sourceCompatibility = Config.sourceCompatibility
                        targetCompatibility = Config.targetCompatibility
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
    }
}
