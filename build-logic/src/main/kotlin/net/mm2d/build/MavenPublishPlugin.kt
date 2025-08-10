package net.mm2d.build

import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

class MavenPublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugin()
    }
}

private fun Project.plugin() {
    with(pluginManager) {
        apply("com.vanniktech.maven.publish")
        apply("org.jetbrains.kotlinx.binary-compatibility-validator")
    }
    mavenPublishing {
        configure(
            AndroidSingleVariantLibrary(
                variant = "release",
                sourcesJar = true,
                publishJavadocJar = true,
            )
        )
        publishToMavenCentral()
        signAllPublications()
        afterEvaluate {
            coordinates(
                groupId = project.group.toString(),
                artifactId = project.base.archivesName.get(),
                version = project.version.toString()
            )
        }
        pom {
            name.set(project.pomName)
            description.set(project.pomDescription)
            url.set(Projects.Url.SITE)
            inceptionYear.set(project.pomInceptionYear)
            licenses {
                license {
                    name.set("The MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set(Projects.DEVELOPER_ID)
                    name.set(Projects.DEVELOPER_NAME)
                }
            }
            scm {
                connection.set(Projects.Url.SCM)
                developerConnection.set(Projects.Url.SCM)
                url.set(Projects.Url.GITHUB)
            }
        }
    }
}

// DSL
private fun Project.mavenPublishing(configure: Action<MavenPublishBaseExtension>): Unit =
    (this as ExtensionAware).extensions.configure("mavenPublishing", configure)

private fun Project.findPropertyString(name: String): String {
    val value = findProperty(name) as? String
    require(!value.isNullOrBlank())
    return value
}

var Project.pomName: String
    get() = findPropertyString("POM_NAME")
    set(value) = setProperty("POM_NAME", value)

var Project.pomDescription: String
    get() = findPropertyString("POM_DESCRIPTION")
    set(value) = setProperty("POM_DESCRIPTION", value)

var Project.pomInceptionYear: String
    get() = findPropertyString("POM_INCEPTION_YEAR")
    set(value) = setProperty("POM_INCEPTION_YEAR", value)
