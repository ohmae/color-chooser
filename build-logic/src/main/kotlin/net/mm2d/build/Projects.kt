package net.mm2d.build

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object Projects {
    const val COMPILE_SDK: Int = 35
    const val MIN_SDK: Int = 21
    const val TARGET_SDK: Int = 35

    val SOURCE_COMPATIBILITY: JavaVersion = JavaVersion.VERSION_1_8
    val TARGET_COMPATIBILITY: JavaVersion = JavaVersion.VERSION_1_8
    val JVM_TARGET: JvmTarget = JvmTarget.JVM_1_8
    const val JVM_TOOLCHAIN: Int = 17

    const val GROUP_ID: String = "net.mm2d.color-chooser"
    const val DEVELOPER_ID: String = "ryo"
    const val DEVELOPER_NAME: String = "ryosuke"

    object Chooser {
        private const val VERSION_MAJOR: Int = 0
        private const val VERSION_MINOR: Int = 7
        private const val VERSION_PATCH: Int = 3
        const val VERSION_NAME: String = "$VERSION_MAJOR.$VERSION_MINOR.$VERSION_PATCH"
    }

    object Compose {
        private const val VERSION_MAJOR: Int = 0
        private const val VERSION_MINOR: Int = 0
        private const val VERSION_PATCH: Int = 6
        const val VERSION_NAME: String = "$VERSION_MAJOR.$VERSION_MINOR.$VERSION_PATCH"
    }

    object Url {
        const val SITE: String = "https://github.com/ohmae/color-chooser"
        const val GITHUB: String = "https://github.com/ohmae/color-chooser"
        const val SCM: String = "scm:git:https://github.com/ohmae/color-chooser.git"
    }
}
