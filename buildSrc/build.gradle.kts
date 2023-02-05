plugins {
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.4.1")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.45.0")
    implementation("org.jetbrains.dokka:dokka-core:1.7.20")
}
