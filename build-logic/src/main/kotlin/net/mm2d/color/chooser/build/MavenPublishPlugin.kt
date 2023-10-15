package net.mm2d.color.chooser.build

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
import java.io.File
import java.net.URI

@Suppress("unused")
class MavenPublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.mavenPublishPlugin()
    }
}

private val Project.publishing: PublishingExtension
    get() = (this as ExtensionAware).extensions.getByName("publishing") as PublishingExtension

private fun Project.publishing(configure: Action<PublishingExtension>): Unit =
    (this as ExtensionAware).extensions.configure("publishing", configure)

private fun Project.signing(configure: Action<SigningExtension>): Unit =
    (this as ExtensionAware).extensions.configure("signing", configure)

private fun Project.mavenPublishPlugin() {
    with(pluginManager) {
        apply("org.gradle.maven-publish")
        apply("org.gradle.signing")
    }
    tasks.create("javadocJar", Jar::class) {
        dependsOn("dokkaJavadoc")
        archiveClassifier.set("javadoc")
        from(File(layout.buildDirectory.asFile.get(), "docs/javadoc"))
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
                    groupId = Projects.groupId
                    artifactId = base.archivesName.get()
                    version = Projects.versionName
                    pom {
                        name.set(Projects.name)
                        description.set(Projects.description)
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
            }
            repositories {
                maven {
                    url =
                        URI("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                    credentials {
                        username =
                            project.findProperty("sonatype_username") as? String ?: ""
                        password =
                            project.findProperty("sonatype_password") as? String ?: ""
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
