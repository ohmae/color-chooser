plugins {
    kotlin("jvm") version "1.5.0"
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
}

dependencies {
    implementation("com.android.tools.build:gradle:4.2.0")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.38.0")
}
