import build.ProjectProperties
import build.base
import build.dependencyUpdatesSettings
import build.publishingSettings

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.gradle.maven-publish")
    id("org.gradle.signing")
    id("org.jetbrains.dokka")
    id("com.github.ben-manes.versions")
}

base.archivesName.set("color-chooser")
group = ProjectProperties.groupId
version = ProjectProperties.versionName

android {
    compileSdk = 34

    namespace = "net.mm2d.color.chooser"
    defaultConfig {
        minSdk = 21
    }
    buildTypes {
        debug {
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
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
    implementation(libs.kotlinStdlib)
    implementation(libs.kotlinxCoroutinesAndroid)

    implementation(libs.androidxAppCompat)
    implementation(libs.androidxConstraintLayout)
    implementation(libs.androidxCoreKtx)
    implementation(libs.androidMaterial)
    testImplementation(libs.junit)
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
