import build.ProjectProperties
import build.base
import build.dependencyUpdatesSettings
import build.publishingSettings
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("com.android.library")
    id("kotlin-android")
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

    defaultConfig {
        minSdk = 21
        targetSdk = 31
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
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.1")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("com.google.android.material:material:1.4.0")
    testImplementation("junit:junit:4.13.2")
}

tasks.named<DokkaTask>("dokkaHtml") {
    outputDirectory.set(File(projectDir, "../docs/dokka"))
    dokkaSourceSets {
        configureEach {
            moduleName.set("color-chooser")
        }
    }
}

tasks.named<DokkaTask>("dokkaJavadoc") {
    outputDirectory.set(File(buildDir, "docs/javadoc"))
    dokkaSourceSets {
        configureEach {
            moduleName.set("color-chooser")
        }
    }
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
