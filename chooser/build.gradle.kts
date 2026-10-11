import chooser.Projects
import chooser.pomDescription
import chooser.pomInceptionYear
import chooser.pomName

plugins {
    alias(libs.plugins.chooser.androidLibrary)
    alias(libs.plugins.chooser.kotlinAndroid)
    alias(libs.plugins.chooser.documentationDokka)
    alias(libs.plugins.chooser.mavenPublish)
    alias(libs.plugins.chooser.dependencyGuard)
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
    implementation(libs.androidxCore)
    implementation(libs.materialComponents)
}

apiValidation {
    ignoredPackages.add("net.mm2d.color.chooser.databinding")
}
