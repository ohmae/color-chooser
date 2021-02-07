plugins {
    kotlin("jvm") version "1.4.30"
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.android.tools.build:gradle:4.1.2")
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.36.0")
}
