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

base.archivesName.set("color-chooser-compose")
group = Projects.groupId
version = Projects.Compose.versionName

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
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
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

dependencyGuard {
    configuration("releaseRuntimeClasspath")
}
