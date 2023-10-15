package net.mm2d.color.chooser.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

@Suppress("unused")
class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }
            androidLibrary {
                android {
                    compileSdk = Projects.compileSdk
                    defaultConfig {
                        minSdk = Projects.minSdk
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
                tasks.create("sourcesJar", Jar::class) {
                    archiveClassifier.set("sources")
                    from(android.sourceSets["main"].java.srcDirs)
                }
            }
        }
    }
}
