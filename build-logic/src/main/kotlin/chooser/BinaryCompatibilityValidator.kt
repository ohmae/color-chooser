package chooser

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import kotlinx.validation.ApiValidationExtension
import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.work.DisableCachingByDefault
import java.nio.file.Files
import javax.inject.Inject

internal fun Project.configureBinaryCompatibilityValidator() {
    val bcvRuntimeClasspath = createBcvRuntimeClasspath()

    val extension = extensions.create("apiValidation", ApiValidationExtension::class.java)
    val androidComponents = extensions.getByType<LibraryAndroidComponentsExtension>()
    androidComponents.onVariants(androidComponents.selector().withName("release")) { variant ->
        val enabled = name !in extension.ignoredProjects && !extension.validationDisabled

        val apiFileName = "$name.api"
        val apiFile = layout.projectDirectory.dir(extension.apiDumpDirectory).file(apiFileName)

        val apiBuild = tasks.register<AndroidApiBuildTask>("apiBuild") {
            isEnabled = enabled
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            outputApiFile.set(layout.buildDirectory.file("api/$apiFileName"))
            runtimeClasspath.from(bcvRuntimeClasspath)
        }
        variant.artifacts.forScope(ScopedArtifacts.Scope.PROJECT)
            .use(apiBuild)
            .toGet(ScopedArtifact.CLASSES, AndroidApiBuildTask::classJars, AndroidApiBuildTask::classDirectories)

        val apiCheck = tasks.register<KotlinApiCompareTask>("apiCheck") {
            isEnabled = enabled
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            projectApiFile.set(apiFile)
            generatedApiFile.set(apiBuild.flatMap { it.outputApiFile })
        }

        val apiDump = tasks.register<SyncFile>("apiDump") {
            isEnabled = enabled
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            from.set(apiBuild.flatMap { it.outputApiFile })
            to.set(apiFile)
        }

        apiCheck.configure { mustRunAfter(apiDump) }
        tasks.named("check").configure { dependsOn(apiCheck) }
    }
}

@DisableCachingByDefault(because = "Matches KotlinApiBuildTask caching behavior")
internal abstract class AndroidApiBuildTask : KotlinApiBuildTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val classJars: ListProperty<RegularFile>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val classDirectories: ListProperty<Directory>

    @get:Inject
    abstract val archiveOperations: ArchiveOperations

    init {
        // BCV はクラスディレクトリを読むため、AGP が返す JAR もファイルツリーとして渡す。
        inputClassesDirs.from(classDirectories, classJars.map { jars -> jars.map { archiveOperations.zipTree(it) } })
    }
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
