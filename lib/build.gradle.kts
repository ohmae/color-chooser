import net.mm2d.color.chooser.build.Projects

plugins {
    id("logic.android.library")
    id("logic.kotlin.android")
    id("logic.documentation.dokka")
    id("logic.maven.publish")
    id("logic.gradle.versions")
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
    implementation(libs.kotlinStdlib)
    implementation(libs.kotlinxCoroutinesAndroid)

    implementation(libs.androidxAppCompat)
    implementation(libs.androidxConstraintLayout)
    implementation(libs.androidxCoreKtx)
    implementation(libs.materialComponents)
    testImplementation(libs.junit)
}
