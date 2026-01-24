import net.mm2d.build.Projects
import net.mm2d.build.pomDescription
import net.mm2d.build.pomInceptionYear
import net.mm2d.build.pomName

plugins {
    alias(libs.plugins.build.logic.androidLibrary)
    alias(libs.plugins.build.logic.kotlinAndroid)
    alias(libs.plugins.build.logic.documentationDokka)
    alias(libs.plugins.build.logic.mavenPublish)
    alias(libs.plugins.build.logic.gradleVersions)
    alias(libs.plugins.build.logic.dependencyGuard)
}

base.archivesName.set("color-chooser")
group = Projects.GROUP_ID
version = Projects.Chooser.VERSION_NAME
pomName = "Color Chooser Dialog"
pomDescription = "Color Chooser Dialog"
pomInceptionYear = "2018"

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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidxAppCompat)
    implementation(libs.androidxConstraintLayout)
    implementation(libs.androidxCoreKtx)
    implementation(libs.materialComponents)
    implementation(libs.androidxDatabinding)
    testImplementation(libs.junit)
}
