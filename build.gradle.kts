buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath(kotlin("gradle-plugin", version = "1.8.20"))
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.8.10")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")

        classpath("com.github.ben-manes:gradle-versions-plugin:0.46.0")
    }
}

tasks.create("clean", Delete::class) {
    delete(rootProject.buildDir)
}
