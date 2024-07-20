plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.bundles.plugins)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "build.logic.androidApplication"
            implementationClass = "net.mm2d.build.AndroidApplicationPlugin"
        }
        register("androidLibrary") {
            id = "build.logic.androidLibrary"
            implementationClass = "net.mm2d.build.AndroidLibraryPlugin"
        }
        register("kotlinAndroid") {
            id = "build.logic.kotlinAndroid"
            implementationClass = "net.mm2d.build.KotlinAndroidPlugin"
        }
        register("navigationSafeArgs") {
            id = "build.logic.navigationSafeArgs"
            implementationClass = "net.mm2d.build.NavigationSafeArgsPlugin"
        }
        register("documentationDokka") {
            id = "build.logic.documentationDokka"
            implementationClass = "net.mm2d.build.DocumentationDokkaPlugin"
        }
        register("mavenPublish") {
            id = "build.logic.mavenPublish"
            implementationClass = "net.mm2d.build.MavenPublishPlugin"
        }
        register("gradleVersions") {
            id = "build.logic.gradleVersions"
            implementationClass = "net.mm2d.build.GradleVersionsPlugin"
        }
        register("dependencyGuard") {
            id = "build.logic.dependencyGuard"
            implementationClass = "net.mm2d.build.DependencyGuardPlugin"
        }
    }
}
