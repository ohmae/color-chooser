plugins {
    id("build.logic.androidApplication")
    id("build.logic.kotlinAndroid")
    id("build.logic.navigationSafeArgs")
    id("build.logic.gradleVersions")
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
