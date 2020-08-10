import build.*
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    maven
    `maven-publish`
    id("org.jetbrains.dokka-android")
    id("com.jfrog.bintray")
    id("com.github.ben-manes.versions")
}

base.archivesBaseName = "color-chooser"
group = ProjectProperties.groupId
version = ProjectProperties.versionName

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(29)
        versionCode = ProjectProperties.versionCode
        versionName = ProjectProperties.versionName
        vectorDrawables.useSupportLibrary = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("com.google.android.material:material:1.2.0")
    testImplementation("junit:junit:4.13")
}

tasks.named<DokkaTask>("dokka") {
    outputFormat = "gfm"
    outputDirectory = "../docs/dokka"
}

commonSettings()
