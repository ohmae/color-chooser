plugins {
    kotlin("jvm") version "1.4.31"
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
    jcenter()
}

dependencies {
    implementation("com.android.tools.build:gradle:4.1.2")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.38.0")
}
