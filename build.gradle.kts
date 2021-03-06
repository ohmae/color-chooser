buildscript {
    repositories {
        google()
        gradlePluginPortal()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.2")
        classpath(kotlin("gradle-plugin", version = "1.4.31"))
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.20")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.3")

        classpath("com.github.ben-manes:gradle-versions-plugin:0.38.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.create("clean", Delete::class) {
    delete(rootProject.buildDir)
}
