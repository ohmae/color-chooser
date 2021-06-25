plugins {
    kotlin("jvm") version "1.5.20"
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.android.tools.build:gradle:4.2.1")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.39.0")
}
