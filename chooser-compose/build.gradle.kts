import net.mm2d.build.Projects
import net.mm2d.build.pomDescription
import net.mm2d.build.pomInceptionYear
import net.mm2d.build.pomName

plugins {
    alias(libs.plugins.build.logic.androidLibrary)
    alias(libs.plugins.build.logic.kotlinAndroid)
    alias(libs.plugins.build.logic.documentationDokka)
    alias(libs.plugins.build.logic.mavenPublish)
    alias(libs.plugins.build.logic.dependencyGuard)
    alias(libs.plugins.kotlinCompose)
}

base.archivesName.set("color-chooser-compose")
group = Projects.GROUP_ID
version = Projects.Compose.VERSION_NAME
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
    implementation(libs.androidxCoreKtx)
    implementation(platform(libs.composeBom))
    implementation(libs.composeUi)
    implementation(libs.composeUiGraphics)
    implementation(libs.composeMaterial3)
    testImplementation(libs.junit)
}
