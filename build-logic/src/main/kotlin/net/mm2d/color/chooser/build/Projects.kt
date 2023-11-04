package net.mm2d.color.chooser.build

import org.gradle.api.JavaVersion

object Projects {
    const val compileSdk: Int = 34
    const val minSdk: Int = 21
    const val targetSdk: Int = 34

    val sourceCompatibility: JavaVersion = JavaVersion.VERSION_11
    val targetCompatibility: JavaVersion = JavaVersion.VERSION_11
    val jvmTarget: String = JavaVersion.VERSION_11.toString()
    const val jdkVersion: Int = 11

    const val groupId: String = "net.mm2d.color-chooser"
    const val name: String = "color chooser"
    const val description: String = "Color chooser dialog library for android"
    const val developerId: String = "ryo"
    const val developerName: String = "ryosuke"

    private const val versionMajor: Int = 0
    private const val versionMinor: Int = 6
    private const val versionPatch: Int = 3
    const val versionName: String = "$versionMajor.$versionMinor.$versionPatch"

    object Url {
        const val site: String = "https://github.com/ohmae/color-chooser"
        const val github: String = "https://github.com/ohmae/color-chooser"
        const val scm: String = "scm:git:https://github.com/ohmae/color-chooser.git"
    }
}
