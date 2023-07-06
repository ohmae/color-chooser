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
    compileSdk = 33

    namespace = "net.mm2d.color.chooser"
    defaultConfig {
        minSdk = 21

        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        debug {
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    kotlin {
        jvmToolchain(11)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
}

tasks.dokkaHtml.configure {
    dokkaSourceSets {
        named("main") {
            noAndroidSdkLink.set(false)
        }
    }
    outputDirectory.set(File(projectDir, "../docs/dokka"))
    moduleName.set("color-chooser")
}

tasks.dokkaJavadoc.configure {
    dokkaSourceSets {
        named("main") {
            noAndroidSdkLink.set(false)
        }
    }
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

publishingSettings()
dependencyUpdatesSettings()
