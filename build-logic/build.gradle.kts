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
            id = "logic.android.application"
            implementationClass = "net.mm2d.color.chooser.build.AndroidApplicationPlugin"
        }
        register("androidLibrary") {
            id = "logic.android.library"
            implementationClass = "net.mm2d.color.chooser.build.AndroidLibraryPlugin"
        }
        register("kotlinAndroid") {
            id = "logic.kotlin.android"
            implementationClass = "net.mm2d.color.chooser.build.KotlinAndroidPlugin"
        }
        register("navigationSafeArgs") {
            id = "logic.navigation.safeArgs"
            implementationClass = "net.mm2d.color.chooser.build.NavigationSafeArgsPlugin"
        }
        register("documentationDokka") {
            id = "logic.documentation.dokka"
            implementationClass = "net.mm2d.color.chooser.build.DocumentationDokkaPlugin"
        }
        register("mavenPublish") {
            id = "logic.maven.publish"
            implementationClass = "net.mm2d.color.chooser.build.MavenPublishPlugin"
        }
        register("gradleVersions") {
            id = "logic.gradle.versions"
            implementationClass = "net.mm2d.color.chooser.build.GradleVersionsPlugin"
        }
    }
}
