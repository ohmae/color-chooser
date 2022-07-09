plugins {
    kotlin("jvm") version "1.7.10"
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.android.tools.build:gradle:7.2.1")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.42.0")
}
