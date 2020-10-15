import build.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("com.github.ben-manes.versions")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        applicationId = "net.mm2d.color.chooser.sample"
        minSdkVersion(16)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        resConfigs("en")
        vectorDrawables.useSupportLibrary = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":lib"))
    implementation(kotlin("stdlib"))
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.2")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.5")
    testImplementation("junit:junit:4.13.1")
}

dependencyUpdates()
