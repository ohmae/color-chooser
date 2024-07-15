import net.mm2d.build.Projects

plugins {
    alias(libs.plugins.build.logic.androidLibrary)
    alias(libs.plugins.build.logic.kotlinAndroid)
    alias(libs.plugins.build.logic.documentationDokka)
    alias(libs.plugins.build.logic.mavenPublish)
    alias(libs.plugins.build.logic.gradleVersions)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dependencyGuard)
}

base.archivesName.set("color-chooser")
group = Projects.groupId
version = Projects.versionName

android {
    namespace = "net.mm2d.color.chooser"
    buildTypes {
        debug {
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(projects.chooserCompose)

    implementation(libs.androidxCoreKtx)
    implementation(libs.androidxAppCompat)
    implementation(libs.materialComponents)
    implementation(platform(libs.composeBom))
    implementation(libs.composeUi)
    implementation(libs.composeUiGraphics)
    implementation(libs.composeMaterial3)

    testImplementation(libs.junit)
}

dependencyGuard {
    configuration("releaseRuntimeClasspath")
}
