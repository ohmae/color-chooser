import build.dependencyUpdatesSettings

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.github.ben-manes.versions")
}

android {
    compileSdk = 31

    namespace = "net.mm2d.color.chooser.sample"
    defaultConfig {
        applicationId = "net.mm2d.color.chooser.sample"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
        resourceConfigurations += "en"
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        debug {
            isDebuggable = true
            isTestCoverageEnabled = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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

dependencies {
    implementation(project(":lib"))
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.1")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.9.1")
    testImplementation("junit:junit:4.13.2")
}

dependencyUpdatesSettings()
