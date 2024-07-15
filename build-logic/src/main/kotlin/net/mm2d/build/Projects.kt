package net.mm2d.build

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

@Suppress("ConstPropertyName")
object Projects {
    const val compileSdk: Int = 34
    const val minSdk: Int = 21
    const val targetSdk: Int = 34

    val sourceCompatibility: JavaVersion = JavaVersion.VERSION_1_8
    val targetCompatibility: JavaVersion = JavaVersion.VERSION_1_8
    val jvmTarget: JvmTarget = JvmTarget.JVM_1_8

    const val groupId: String = "net.mm2d.color-chooser"
    const val name: String = "color chooser"
    const val description: String = "Color chooser dialog library for android"
    const val developerId: String = "ryo"
    const val developerName: String = "ryosuke"

    private const val versionMajor: Int = 0
    private const val versionMinor: Int = 7
    private const val versionPatch: Int = 3
    const val versionName: String = "$versionMajor.$versionMinor.$versionPatch"

    object Url {
        const val site: String = "https://github.com/ohmae/color-chooser"
        const val github: String = "https://github.com/ohmae/color-chooser"
        const val scm: String = "scm:git:https://github.com/ohmae/color-chooser.git"
    }
}
