/*
 * Copyright © MobiLab Solutions GmbH
 */

buildscript {

    repositories {
        google()
        jcenter()
        maven(url = "https://maven.fabric.io/public")
    }

    dependencies {
        classpath(Libs.androidGradlePlugin)
        classpath(Libs.Kotlin.gradlePlugin)
        classpath(Libs.Kotlin.extensions)
        classpath(Libs.AndroidX.Navigation.safeArgs)
        classpath(Libs.Google.fabricPlugin)
        classpath(Libs.gradleVersionsPlugin)
        classpath(Libs.licencePlugin)
        classpath(Libs.dokkaPlugin)
    }
}

plugins {
    id("com.diffplug.gradle.spotless") version ("3.25.0")
    id("com.github.ben-manes.versions") version ("0.26.0")
}

allprojects {
    extra["signing.keyId"] = "CCE33870"
    extra["signing.secretKeyRingFile"] = rootProject.file("signing/secring.gpg")
    extra["signing.password"] = "android"

    repositories {
        google()
        jcenter()
        maven(url = "https://ci.android.com/builds/submitted/5799541/androidx_snapshot/latest/repository")
    }
}

subprojects {
    apply(plugin = "com.diffplug.gradle.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            ktlint("0.31.0")
        }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xuse-experimental=kotlinx.coroutines.FlowPreview"
            )
        }
    }
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}
