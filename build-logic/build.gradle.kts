import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "11"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(libs.bundles.plugins)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "build.logic.androidApplication"
            implementationClass = "net.mm2d.build.AndroidApplicationPlugin"
        }
        register("androidLibrary") {
            id = "build.logic.androidLibrary"
            implementationClass = "net.mm2d.build.AndroidLibraryPlugin"
        }
        register("kotlinAndroid") {
            id = "build.logic.kotlinAndroid"
            implementationClass = "net.mm2d.build.KotlinAndroidPlugin"
        }
        register("navigationSafeArgs") {
            id = "build.logic.navigationSafeArgs"
            implementationClass = "net.mm2d.build.NavigationSafeArgsPlugin"
        }
        register("documentationDokka") {
            id = "build.logic.documentationDokka"
            implementationClass = "net.mm2d.build.DocumentationDokkaPlugin"
        }
        register("mavenPublish") {
            id = "build.logic.mavenPublish"
            implementationClass = "net.mm2d.build.MavenPublishPlugin"
        }
        register("ktlint") {
            id = "build.logic.ktlint"
            implementationClass = "net.mm2d.build.KtlintPlugin"
        }
        register("gradleVersions") {
            id = "build.logic.gradleVersions"
            implementationClass = "net.mm2d.build.GradleVersionsPlugin"
        }
    }
}
