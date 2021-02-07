import build.*
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("com.android.library")
    id("kotlin-android")
    maven
    `maven-publish`
    id("org.jetbrains.dokka")
    id("com.jfrog.bintray")
    id("com.github.ben-manes.versions")
}

base.archivesBaseName = "color-chooser"
group = ProjectProperties.groupId
version = ProjectProperties.versionName

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(30)
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
    buildFeatures {
        viewBinding = true
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
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("com.google.android.material:material:1.3.0")
    testImplementation("junit:junit:4.13.1")
}

tasks.named<DokkaTask>("dokkaHtml") {
    outputDirectory.set(File(projectDir, "../docs/dokka"))
    dokkaSourceSets {
        configureEach {
            moduleName.set("color-chooser")
        }
    }
}

tasks.create("sourcesJar", Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

artifacts {
    archives(tasks.named<Jar>("sourcesJar"))
}

uploadArchivesSettings()
publishingSettings()
bintraySettings()
dependencyUpdatesSettings()
