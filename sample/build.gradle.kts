import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("logic.android.application")
    id("logic.kotlin.android")
    id("logic.navigation.safeArgs")
    id("logic.gradle.versions")
}

android {
    namespace = "net.mm2d.color.chooser.sample"
    defaultConfig {
        applicationId = "net.mm2d.color.chooser.sample"
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        debug {
            isDebuggable = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":lib"))
    implementation(libs.androidxAppCompat)
    implementation(libs.androidxConstraintLayout)
    implementation(libs.androidxNavigationFragmentKtx)
    implementation(libs.androidxNavigationUiKtx)
    implementation(libs.materialComponents)
    debugImplementation(libs.leakCanary)
    testImplementation(libs.junit)
}
