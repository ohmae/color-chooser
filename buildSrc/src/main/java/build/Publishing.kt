package build

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named
import org.gradle.plugins.signing.SigningExtension
import java.net.URI

private fun Project.publishing(configure: PublishingExtension.() -> Unit): Unit =
    (this as ExtensionAware).extensions.configure("publishing", configure)

val Project.base: BasePluginExtension
    get() = (this as ExtensionAware).extensions.getByName("base") as BasePluginExtension

private val NamedDomainObjectContainer<Configuration>.api: NamedDomainObjectProvider<Configuration>
    get() = named<Configuration>("api")

private val NamedDomainObjectContainer<Configuration>.implementation: NamedDomainObjectProvider<Configuration>
    get() = named<Configuration>("implementation")

fun Project.signing(configure: SigningExtension.() -> Unit): Unit =
    (this as ExtensionAware).extensions.configure("signing", configure)

val Project.publishing: PublishingExtension
    get() = (this as ExtensionAware).extensions.getByName("publishing") as PublishingExtension

fun Project.publishingSettings() {
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
                    groupId = ProjectProperties.groupId
                    artifactId = base.archivesName.get()
                    version = ProjectProperties.versionName
                    pom {
                        name.set(ProjectProperties.name)
                        description.set(ProjectProperties.description)
                        url.set(ProjectProperties.Url.site)
                        licenses {
                            license {
                                name.set("The MIT License")
                                url.set("https://opensource.org/licenses/MIT")
                                distribution.set("repo")
                            }
                        }
                        developers {
                            developer {
                                id.set(ProjectProperties.developerId)
                                name.set(ProjectProperties.developerName)
                            }
                        }
                        scm {
                            connection.set(ProjectProperties.Url.scm)
                            developerConnection.set(ProjectProperties.Url.scm)
                            url.set(ProjectProperties.Url.github)
                        }
                    }
                }
            }
            repositories {
                maven {
                    url = URI("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                    credentials {
                        username = project.findProperty("sonatype_username") as? String ?: ""
                        password = project.findProperty("sonatype_password") as? String ?: ""
                    }
                }
            }
        }
        signing {
            sign(publishing.publications["mavenJava"])
        }
    }
}
