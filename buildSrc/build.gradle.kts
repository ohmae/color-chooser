plugins {
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.1.1")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.47.0")
    implementation("org.jetbrains.dokka:dokka-core:1.8.20")
}
