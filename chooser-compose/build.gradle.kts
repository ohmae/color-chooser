import chooser.Projects
import chooser.Projects.Compose
import chooser.pomDescription
import chooser.pomInceptionYear
import chooser.pomName

plugins {
    alias(libs.plugins.chooser.androidLibrary)
    alias(libs.plugins.chooser.kotlinAndroid)
    alias(libs.plugins.chooser.documentationDokka)
    alias(libs.plugins.chooser.mavenPublish)
    alias(libs.plugins.chooser.dependencyGuard)
    alias(libs.plugins.kotlinCompose)
}

base.archivesName.set("color-chooser-compose")
group = Projects.GROUP_ID
version = Compose.VERSION_NAME
pomName = "Color Chooser for Jetpack Compose"
pomDescription = "Color Chooser for Jetpack Compose"
pomInceptionYear = "2024"

android {
    namespace = "net.mm2d.color.chooser.compose"
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
    api(libs.androidxComposeRuntime)
    api(libs.androidxComposeUi)
    api(libs.androidxComposeUiGraphics)
    api(libs.androidxComposeUiText)
    api(libs.androidxComposeUiUnit)
    implementation(libs.androidxComposeMaterial3)
    debugImplementation(libs.androidxComposeUiTooling)
    implementation(libs.androidxComposeUiToolingPreview)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidxComposeUiTestJunit4)
    testImplementation(libs.androidxActivityCompose)
    debugImplementation(libs.androidxComposeUiTestManifest)
}
