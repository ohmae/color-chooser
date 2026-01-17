package net.mm2d.build

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

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
        compileSdk = Projects.COMPILE_SDK

        defaultConfig {
            minSdk = Projects.MIN_SDK
            targetSdk = Projects.TARGET_SDK
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
    }
}

// DSL
private fun Project.androidApplication(configure: Action<ApplicationExtension>): Unit =
    (this as ExtensionAware).extensions.configure("android", configure)
