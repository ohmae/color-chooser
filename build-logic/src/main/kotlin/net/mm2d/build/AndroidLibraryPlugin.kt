package net.mm2d.build

import com.android.build.gradle.internal.dsl.LibraryExtensionImpl
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugin()
    }
}

private fun Project.plugin() {
    with(pluginManager) {
        apply("com.android.library")
    }
    androidLibrary {
        compileSdk = Projects.COMPILE_SDK
        defaultConfig {
            minSdk = Projects.MIN_SDK
        }
        compileOptions {
            sourceCompatibility = Projects.SOURCE_COMPATIBILITY
            targetCompatibility = Projects.TARGET_COMPATIBILITY
        }
        lint {
            abortOnError = true
        }
        testOptions {
            unitTests.isIncludeAndroidResources = true
        }
        tasks.register("sourcesJar", Jar::class) {
            archiveClassifier.set("sources")
            from(android.sourceSets["main"].java.srcDirs)
        }
        resourcePrefix = "mm2d_cc_"
    }
}

// DSL
private fun Project.androidLibrary(configure: Action<LibraryExtensionImpl>): Unit =
    (this as ExtensionAware).extensions.configure("android", configure)

private val Project.android: LibraryExtensionImpl
    get() = (this as ExtensionAware).extensions.getByName("android") as LibraryExtensionImpl
