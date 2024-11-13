plugins {
    alias(libs.plugins.build.logic.androidApplication)
    alias(libs.plugins.build.logic.kotlinAndroid)
    alias(libs.plugins.build.logic.navigationSafeArgs)
    alias(libs.plugins.build.logic.gradleVersions)
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(projects.chooser)
    implementation(libs.androidxAppCompat)
    implementation(libs.androidxConstraintLayout)
    implementation(libs.androidxNavigationFragmentKtx)
    implementation(libs.androidxNavigationUiKtx)
    implementation(libs.materialComponents)
    debugImplementation(libs.leakCanary)
    testImplementation(libs.junit)
}
