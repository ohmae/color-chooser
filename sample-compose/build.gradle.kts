plugins {
    alias(libs.plugins.build.logic.androidApplication)
    alias(libs.plugins.build.logic.kotlinAndroid)
    alias(libs.plugins.kotlinCompose)
}

android {
    namespace = "net.mm2d.color.chooser.sample.compose"

    defaultConfig {
        applicationId = "net.mm2d.color.chooser.sample.compose"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.chooserCompose)

    implementation(libs.androidxCoreKtx)
    implementation(libs.androidxAppCompat)
    implementation(libs.androidxActivityCompose)
    implementation(libs.materialComponents)
    implementation(platform(libs.composeBom))
    implementation(libs.composeUi)
    implementation(libs.composeUiGraphics)
    implementation(libs.composeMaterial3)
}
