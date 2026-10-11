plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.bundles.plugins)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "chooser.androidApplication"
            implementationClass = "chooser.AndroidApplicationPlugin"
        }
        register("androidLibrary") {
            id = "chooser.androidLibrary"
            implementationClass = "chooser.AndroidLibraryPlugin"
        }
        register("kotlinAndroid") {
            id = "chooser.kotlinAndroid"
            implementationClass = "chooser.KotlinAndroidPlugin"
        }
        register("navigationSafeArgs") {
            id = "chooser.navigationSafeArgs"
            implementationClass = "chooser.NavigationSafeArgsPlugin"
        }
        register("documentationDokka") {
            id = "chooser.documentationDokka"
            implementationClass = "chooser.DocumentationDokkaPlugin"
        }
        register("mavenPublish") {
            id = "chooser.mavenPublish"
            implementationClass = "chooser.MavenPublishPlugin"
        }
        register("dependencyGuard") {
            id = "chooser.dependencyGuard"
            implementationClass = "chooser.DependencyGuardPlugin"
        }
    }
}
