import net.mm2d.build.Projects

plugins {
    alias(libs.plugins.build.logic.androidLibrary)
    alias(libs.plugins.build.logic.kotlinAndroid)
    alias(libs.plugins.build.logic.documentationDokka)
    alias(libs.plugins.build.logic.mavenPublish)
    alias(libs.plugins.build.logic.gradleVersions)
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

dependencyGuard {
    configuration("releaseRuntimeClasspath")
}
