package chooser

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureAndroidCommon() {
    extensions.configure<CommonExtension> {
        compileSdk {
            version = release(Projects.COMPILE_SDK)
        }
        defaultConfig.minSdk = Projects.MIN_SDK
        compileOptions.apply {
            sourceCompatibility = Projects.SOURCE_COMPATIBILITY
            targetCompatibility = Projects.TARGET_COMPATIBILITY
        }
        lint.abortOnError = true
        testOptions.unitTests.isIncludeAndroidResources = true
    }
    extensions.configure<KotlinAndroidProjectExtension> {
        compilerOptions {
            jvmTarget.set(Projects.JVM_TARGET)
        }
        jvmToolchain(Projects.JVM_TOOLCHAIN)
    }
    dependencies {
        add("implementation", libs.library("kotlinxCoroutinesAndroid"))
    }
}
