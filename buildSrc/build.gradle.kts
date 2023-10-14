plugins {
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.1.2")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.49.0")
    implementation("org.jetbrains.dokka:dokka-core:1.8.20")
}
