pluginManagement {
    includeBuild("build-logic")
    @Suppress("UnstableApiUsage")
    repositories {
        google {
            content {
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("androidx")
            }
        }
        gradlePluginPortal().content {
            includeGroupAndSubgroups("com.github.ben-manes")
            includeGroupAndSubgroups("org.gradle.toolchains")
        }
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google().content {
            includeGroupAndSubgroups("com.android")
            includeGroupAndSubgroups("com.google")
            includeGroupAndSubgroups("androidx")
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "color-chooser"
include(":chooser")
include(":chooser-compose")
include(":sample")
include(":sample-compose")
