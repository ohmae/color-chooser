package chooser

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(
        target: Project,
    ): Unit =
        with(target) {
            pluginManager.apply("com.android.application")
            configureAndroidCommon()
            extensions.configure<ApplicationExtension> {
                defaultConfig.targetSdk = Projects.TARGET_SDK
            }
        }
}
