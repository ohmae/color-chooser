import build.ProjectProperties
import build.base
import build.dependencyUpdatesSettings
import build.publishingSettings

plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
    id("com.github.ben-manes.versions")
}

base.archivesName.set("color-chooser")
group = ProjectProperties.groupId
version = ProjectProperties.versionName

android {
    compileSdk = 31

    namespace = "net.mm2d.color.chooser"
    defaultConfig {
        minSdk = 21
        targetSdk = 31
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        debug {
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
    implementation(kotlin("stdlib"))

    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.0")
    implementation("com.google.android.material:material:1.6.1")
    testImplementation("junit:junit:4.13.2")
}

tasks.dokkaHtml.configure {
    outputDirectory.set(File(projectDir, "../docs/dokka"))
    moduleName.set("color-chooser")
}

tasks.dokkaJavadoc.configure {
    outputDirectory.set(File(buildDir, "docs/javadoc"))
    moduleName.set("color-chooser")
}

tasks.create("javadocJar", Jar::class) {
    dependsOn("dokkaJavadoc")
    archiveClassifier.set("javadoc")
    from(File(buildDir, "docs/javadoc"))
}

tasks.create("sourcesJar", Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

artifacts {
    archives(tasks.named<Jar>("sourcesJar"))
}

publishingSettings()
dependencyUpdatesSettings()
