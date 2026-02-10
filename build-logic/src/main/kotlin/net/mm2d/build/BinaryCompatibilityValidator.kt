package net.mm2d.build

import kotlinx.validation.ApiValidationExtension
import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.work.DisableCachingByDefault
import java.nio.file.Files

internal fun Project.configureBinaryCompatibilityValidator() {
    val bcvRuntimeClasspath = createBcvRuntimeClasspath()

    val projectName = name
    val extension = extensions.create("apiValidation", ApiValidationExtension::class.java)
    val enabled = projectName !in extension.ignoredProjects && !extension.validationDisabled

    val apiFileName = "$projectName.api"
    val apiDir = layout.projectDirectory.dir(extension.apiDumpDirectory)
    val apiFile = apiDir.file(apiFileName)
    val intermediatePaths = listOf(
        "intermediates/built_in_kotlinc/release/compileReleaseKotlin/classes",
        "intermediates/javac/release/compileReleaseJavaWithJavac/classes",
    )

    val apiBuild = tasks.register<KotlinApiBuildTask>("apiBuild") {
        dependsOn("compileReleaseJavaWithJavac")
        isEnabled = enabled
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        inputClassesDirs.from(
            *intermediatePaths.map { layout.buildDirectory.dir(it) }.toTypedArray()
        )
        outputApiFile.set(layout.buildDirectory.dir("api").map { it.file(apiFileName) })
        runtimeClasspath.from(bcvRuntimeClasspath)
    }

    val apiCheck = tasks.register<KotlinApiCompareTask>("apiCheck") {
        dependsOn(apiBuild)
        isEnabled = enabled
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        projectApiFile.set(apiFile)
        generatedApiFile.set(apiBuild.flatMap { it.outputApiFile })
    }

    tasks.register<SyncFile>("apiDump") {
        isEnabled = enabled
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        from.set(apiBuild.flatMap { it.outputApiFile })
        to.set(apiFile)
    }

    tasks.named("check").configure { dependsOn(apiCheck) }
}

private fun Project.createBcvRuntimeClasspath(): Configuration {
    val configurationName = "bcvRuntimeClasspath"
    val runtimeClasspath = configurations.create(configurationName) {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    dependencies.add(configurationName, "org.ow2.asm:asm:9.9.1")
    dependencies.add(configurationName, "org.ow2.asm:asm-tree:9.9.1")
    dependencies.add(configurationName, "org.jetbrains.kotlin:kotlin-metadata-jvm:2.3.0")
    return runtimeClasspath
}

@DisableCachingByDefault(because = "No computations, only copying files")
internal abstract class SyncFile : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val from: RegularFileProperty

    @get:OutputFile
    abstract val to: RegularFileProperty

    @Suppress("NewApi")
    @TaskAction
    fun copy() {
        val fromFile = from.asFile.get()
        val toFile = to.asFile.get()
        if (fromFile.exists()) {
            fromFile.copyTo(toFile, overwrite = true)
        } else {
            Files.deleteIfExists(toFile.toPath())
        }
    }
}
