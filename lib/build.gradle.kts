import net.mm2d.build.Projects

plugins {
    id("build.logic.androidLibrary")
    id("build.logic.kotlinAndroid")
    id("build.logic.documentationDokka")
    id("build.logic.mavenPublish")
    id("build.logic.gradleVersions")
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
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.androidxAppCompat)
    implementation(libs.androidxConstraintLayout)
    implementation(libs.androidxCoreKtx)
    implementation(libs.materialComponents)
    testImplementation(libs.junit)
}
