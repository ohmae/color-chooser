package chooser

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(
        target: Project,
    ): Unit =
        with(target) {
            pluginManager.apply("com.android.library")
            configureAndroidCommon()
            extensions.configure<LibraryExtension> {
                resourcePrefix = "mm2d_cc_"
            }
            configureBinaryCompatibilityValidator()
        }
}
