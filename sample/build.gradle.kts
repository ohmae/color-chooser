import build.dependencyUpdatesSettings

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.github.ben-manes.versions")
}

android {
    compileSdk = 30

    defaultConfig {
        applicationId = "net.mm2d.color.chooser.sample"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
        resourceConfigurations += "en"
        vectorDrawables.useSupportLibrary = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":lib"))
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
    testImplementation("junit:junit:4.13.2")
}

dependencyUpdatesSettings()
