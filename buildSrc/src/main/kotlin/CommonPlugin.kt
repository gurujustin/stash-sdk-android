import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.jaredsburrows.license.LicenseReportExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.KotlinClosure1
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-08-2019.
 */
@Suppress("unused")
class CommonPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.configureKotlin()
        project.configureLicenseReport()
        project.plugins.all { plugin: Plugin<*>? ->
            when (plugin) {
                is LibraryPlugin -> {
                    project.extensions.getByType<LibraryExtension>().apply {
                        configureCommons(project)
                    }
                }
                is AppPlugin -> {
                    project.extensions.getByType<BaseExtension>().apply {
                        configureCommons(project)
                    }
                }
            }
            true
        }
    }

    private fun BaseExtension.configureCommons(project: Project) {
        compileSdkVersion(StashBuildConfigs.compileSdk)
        buildToolsVersion(StashBuildConfigs.buildtoolsVersion)

        defaultConfig {
            minSdkVersion(StashBuildConfigs.minSdk)
            targetSdkVersion(StashBuildConfigs.targetSdk)

            versionCode = project.propOrDefWithTravis(StashRelease.travisBuildNumber, StashRelease.versionCode).toInt()
            versionName = project.sdkVersionName()

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            buildConfigField("String", "mobilabBackendUrl", "\"" + project.propOrDefWithTravis(StashRelease.mobilabBackendUrl, "") + "\"")
            buildConfigField("String", "testPublishableKey", "\"" + project.propOrDefWithTravis(StashRelease.testPublishableKey, "") + "\"")
            buildConfigField("String", "newBsApiUrl", "\"" + project.propOrDefWithTravis(StashRelease.newBsApiUrl, "") + "\"")
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        lintOptions {
            isAbortOnError = false
        }

        testOptions {
            unitTests.apply {
                isIncludeAndroidResources = true
                all(KotlinClosure1<Any, Test>({
                    (this as Test).also {
                        maxHeapSize = "1024m"
                        testLogging {
                            events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED)
                        }
                    }
                }, this))
            }
        }
    }

    private fun Project.configureLicenseReport() {
        apply(plugin = "com.jaredsburrows.license")
        configure<LicenseReportExtension> {
            generateHtmlReport = true
            generateJsonReport = true

            copyHtmlReportToAssets = false
            copyJsonReportToAssets = false
        }
    }

    private fun Project.configureKotlin() {
        apply(plugin = "kotlin-android")
        apply(plugin = "kotlin-android-extensions")
        configure<AndroidExtensionsExtension> {
            isExperimental = true
        }

        apply(plugin = "kotlin-kapt")
        configure<KaptExtension> {
            correctErrorTypes = true
            useBuildCache = true
        }
    }
}