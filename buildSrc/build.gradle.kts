plugins {
    kotlin("jvm") version "1.5.21"
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.android.tools.build:gradle:7.0.0")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.39.0")
}
