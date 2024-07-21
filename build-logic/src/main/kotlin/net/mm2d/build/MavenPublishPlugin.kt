package net.mm2d.build

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.plugins.signing.SigningExtension
import java.net.URI

class MavenPublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugin()
    }
}

private fun Project.plugin() {
    with(pluginManager) {
        apply("org.gradle.maven-publish")
        apply("org.gradle.signing")
    }
    tasks.create("javadocJar", Jar::class) {
        dependsOn("dokkaJavadoc")
        archiveClassifier.set("javadoc")
    }
    tasks.named("publish") {
        dependsOn("assemble")
        dependsOn("javadocJar")
        dependsOn("sourcesJar")
    }
    afterEvaluate {
        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["release"])
                    applyProjectProperty(this@plugin)
                }
            }
            repositories {
                maven {
                    url = URI("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                    credentials {
                        username = findPropertyString("ossrh_username")
                        password = findPropertyString("ossrh_password")
                    }
                }
            }
        }
        signing {
            sign(publishing.publications["mavenJava"])
        }
        tasks.named("signMavenJavaPublication") {
            dependsOn("bundleReleaseAar")
        }
    }
}

private fun MavenPublication.applyProjectProperty(project: Project) {
    groupId = project.group.toString()
    artifactId = project.base.archivesName.get()
    version = project.version.toString()
    pom {
        name.set(project.pomName)
        description.set(project.pomDescription)
        url.set(Projects.Url.site)
        licenses {
            license {
                name.set("The MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set(Projects.developerId)
                name.set(Projects.developerName)
            }
        }
        scm {
            connection.set(Projects.Url.scm)
            developerConnection.set(Projects.Url.scm)
            url.set(Projects.Url.github)
        }
    }
}

// DSL
private val Project.publishing: PublishingExtension
    get() = (this as ExtensionAware).extensions.getByName("publishing") as PublishingExtension

private fun Project.publishing(configure: Action<PublishingExtension>): Unit =
    (this as ExtensionAware).extensions.configure("publishing", configure)

private fun Project.signing(configure: Action<SigningExtension>): Unit =
    (this as ExtensionAware).extensions.configure("signing", configure)

private fun Project.findPropertyString(name: String, defaultValue: String = ""): String =
    (findProperty(name) as String?).let {
        if (it.isNullOrBlank()) defaultValue else it
    }

var Project.pomName: String
    get() = findPropertyString("POM_NAME", Projects.name)
    set(value) = setProperty("POM_NAME", value)

var Project.pomDescription: String
    get() = findPropertyString("POM_DESCRIPTION", Projects.description)
    set(value) = setProperty("POM_DESCRIPTION", value)
