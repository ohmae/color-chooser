package net.mm2d.color.chooser.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import java.io.File
import java.net.URI

@Suppress("unused")
class MavenPublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
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
                            groupId = Config.groupId
                            artifactId = base.archivesName.get()
                            version = Config.versionName
                            pom {
                                name.set(Config.name)
                                description.set(Config.description)
                                url.set(Config.Url.site)
                                licenses {
                                    license {
                                        name.set("The MIT License")
                                        url.set("https://opensource.org/licenses/MIT")
                                        distribution.set("repo")
                                    }
                                }
                                developers {
                                    developer {
                                        id.set(Config.developerId)
                                        name.set(Config.developerName)
                                    }
                                }
                                scm {
                                    connection.set(Config.Url.scm)
                                    developerConnection.set(Config.Url.scm)
                                    url.set(Config.Url.github)
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
    }
}
