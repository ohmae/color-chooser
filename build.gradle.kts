buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.androidGradlePlugin)
        classpath(libs.kotlinGradlePlugin)
        classpath(libs.dokkaGradlePlugin)
        classpath(libs.navigationSafeArgsGradlePlugin)

        classpath(libs.gradleVersionsPlugin)
    }
}
