plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinCompose) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktechMavenPublish) apply false
    alias(libs.plugins.navigationSafeArgs) apply false
    alias(libs.plugins.gradleVersions) apply false
    alias(libs.plugins.dependencyGuard) apply false
}

buildscript {
    dependencies {
        classpath(libs.kotlinGradlePlugin)
    }
}

dependencies {
    val dokkaPluginId = libs.plugins.build.logic.documentationDokka.get().pluginId
    subprojects {
        plugins.withId(dokkaPluginId) {
            dokka(this@subprojects)
        }
    }
}

dokka {
    basePublicationsDirectory.set(File(projectDir, "docs/dokka"))
}

val ktlint: Configuration by configurations.creating

dependencies {
    ktlint(libs.ktlint) {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}

tasks.register<JavaExec>("ktlint") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args(
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
    )
    isIgnoreExitValue = true
}

tasks.register<JavaExec>("ktlintFormat") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style and format"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    args(
        "-F",
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
    )
    isIgnoreExitValue = true
}
