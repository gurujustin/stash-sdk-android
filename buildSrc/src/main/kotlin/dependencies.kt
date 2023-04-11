/*
 * Copyright © MobiLab Solutions GmbH
 */

import org.gradle.api.Project
import java.util.Properties

object StashRelease {
    const val travisBuildNumber = "TRAVIS_BUILD_NUMBER"
    const val travisTag = "TRAVIS_TAG"
    const val mobilabBackendUrl = "DEV_BACKEND_URL"
    const val newBsApiUrl = "NEW_BS_API_URL"
    const val testPublishableKey = "DEV_PUBLISHABLE_KEY"
    const val MobilabNexusUsername = "MOBILAB_NEXUS_USER"
    const val MobilabNexusPassword = "MOBILAB_NEXUS_PASSWORD"

    const val versionCode = "1"
    const val versionName = "0.15"
}

object StashBuildConfigs {
    const val compileSdk = 28
    const val minSdk = 21
    const val targetSdk = 28
    const val buildtoolsVersion = "29.0.2"
}

object DemoRelease {
    const val fabricApiKey = "FABRIC_API_KEY"
    const val versionCode = "1"
    const val versionName = "0.14" // 0.<Sprint number>
}

val isTravisBuild: Boolean = System.getenv("TRAVIS") == "true"

val isTravisTag: Boolean = !System.getenv("TRAVIS_TAG").isNullOrBlank()

object Modules {
    const val bsPayoneIntegration = ":bspayone-integration"
    const val braintreeIntegration = ":braintree-integration"
    const val adyenIntegration = ":adyen-integration"
    const val stash = ":lib"
}

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:3.5.2"
    const val gradleVersionsPlugin = "com.github.ben-manes:gradle-versions-plugin:0.25.0"
    const val timber = "com.jakewharton.timber:timber:4.7.1"
    const val junit = "junit:junit:4.12"
    const val mockitoCore = "org.mockito:mockito-core:3.1.0"
    const val robolectric = "org.robolectric:robolectric:4.3.1"
    const val threetenabp = "com.jakewharton.threetenabp:threetenabp:1.2.1"
    const val iban4j = "org.iban4j:iban4j:3.2.1"
    const val braintree = "com.braintreepayments.api:braintree:3.6.0"
    const val mvrx = "com.airbnb.android:mvrx:1.2.1"
    const val caligraphy = "io.github.inflationx:calligraphy3:3.1.1"
    const val viewPump = "io.github.inflationx:viewpump:2.0.3"
    const val licencePlugin = "com.jaredsburrows:gradle-license-plugin:0.8.5"
    const val dokkaPlugin = "org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.18"

    object Adyen {
        private const val version = "3.3.1"
        val thressDs2 = "com.adyen.checkout:3ds2:$version"
        val card = "com.adyen.checkout:card-ui:$version"
        val redirect = "com.adyen.checkout:redirect:$version"
    }

    object Google {
        const val material = "com.google.android.material:material:1.1.0-alpha10"
        const val crashlytics = "com.crashlytics.sdk.android:crashlytics:2.10.1"
        const val fabricPlugin = "io.fabric.tools:gradle:1.31.1"
    }

    object Kotlin {
        private const val version = "1.3.50"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
        const val test = "org.jetbrains.kotlin:kotlin-test-junit:$version"
    }

    object Coroutines {
        private const val version = "1.3.1"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val rx2 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.1.0"
        const val recyclerview = "androidx.recyclerview:recyclerview:1.1.0-beta04"
        const val cardview = "androidx.cardview:cardview:1.0.0"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta2"
        const val coreKtx = "androidx.core:core-ktx:1.2.0-alpha04"
        const val preference = "androidx.preference:preference:1.1.0"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0-alpha02"

        object Navigation {
            private const val version = "2.2.0-alpha03"
            const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val ui = "androidx.navigation:navigation-ui-ktx:$version"
            const val safeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:$version"
        }

        object Test {
            const val core = "androidx.test:core:1.2.1-alpha02"
            const val coreKtx = "androidx.test:core:1.2.0"
            const val ext = "androidx.test.ext:junit-ktx:1.1.2-alpha02"
            const val runner = "androidx.test:runner:1.3.0-alpha02"
            const val rules = "androidx.test:rules:1.3.0-alpha02"

            const val espressoCore = "androidx.test.espresso:espresso-core:3.3.0-alpha02"
            const val espressoIntents = "androidx.test.espresso:espresso-intents:3.3.0-alpha02"

            const val uiAutomator = "androidx.test.uiautomator:uiautomator:2.2.0"
        }

        object Lifecycle {
            private const val version = "2.2.0-alpha05"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
            const val compiler = "androidx.lifecycle:lifecycle-compiler:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
        }

        object Room {
            private const val version = "2.2.0-rc01"
            const val common = "androidx.room:room-common:$version"
            const val runtime = "androidx.room:room-runtime:$version"
            const val rxjava2 = "androidx.room:room-rxjava2:$version"
            const val compiler = "androidx.room:room-compiler:$version"
            const val ktx = "androidx.room:room-ktx:$version"
        }
    }

    object RxJava {
        const val rxJava = "io.reactivex.rxjava2:rxjava:2.2.12"
        const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:2.4.0"
        const val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
    }

    object Dagger {
        private const val version = "2.24"
        const val dagger = "com.google.dagger:dagger:$version"
        const val daggerAndroid = "com.google.dagger:dagger-android:$version"
        const val androidSupport = "com.google.dagger:dagger-android-support:$version"
        const val compiler = "com.google.dagger:dagger-compiler:$version"
        const val androidProcessor = "com.google.dagger:dagger-android-processor:$version"
    }

    object Retrofit {
        private const val version = "2.6.1"
        const val retrofit = "com.squareup.retrofit2:retrofit:$version"
        const val retrofit_rxjava_adapter = "com.squareup.retrofit2:adapter-rxjava2:$version"
        const val gsonConverter = "com.squareup.retrofit2:converter-gson:$version"
    }

    object OkHttp {
        private const val version = "4.2.0"
        const val okhttp = "com.squareup.okhttp3:okhttp:$version"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$version"
        const val mockwebserver = "com.squareup.okhttp3:mockwebserver:$version"
    }

    object Stetho {
        private const val version = "1.5.1"
        const val stetho = "com.facebook.stetho:stetho:$version"
        const val okhttp3 = "com.facebook.stetho:stetho-okhttp3:$version"
    }

    object PowerMock {
        private const val version = "2.0.2"
        const val module = "org.powermock:powermock-module-junit4:$version"
        const val api = "org.powermock:powermock-api-mockito2:$version"
    }

    object AssistedInject {
        private const val version = "0.5.0"
        const val annotationDagger2 = "com.squareup.inject:assisted-inject-annotations-dagger2:$version"
        const val processorDagger2 = "com.squareup.inject:assisted-inject-processor-dagger2:$version"
    }

    object Epoxy {
        private const val version = "3.8.0"
        const val epoxy = "com.airbnb.android:epoxy:$version"
        const val dataBinding = "com.airbnb.android:epoxy-databinding:$version"
        const val processor = "com.airbnb.android:epoxy-processor:$version"
    }

    object Utils {
        const val commonsValidator = "commons-validator:commons-validator:1.6"
    }
}

fun Project.propOrDefWithTravis(propertyName: String, defaultValue: String): String {
    val propertyValue: String?
    propertyValue = if (isTravisBuild) {
        System.getenv(propertyName)
    } else {
        try {
            val properties = Properties()
            properties.load(rootProject.file("local.properties").inputStream())
            properties.getProperty(propertyName)
        } catch (e: Exception) {
            null
        }
    }
    return if (propertyValue == null || propertyValue.isEmpty()) defaultValue else propertyValue
}

fun Project.sdkVersionName(): String {
    val versionCode = propOrDefWithTravis(StashRelease.travisBuildNumber, StashRelease.versionCode)
    val versionName = StashRelease.versionName + "-$versionCode"

    val sdkVersionName = propOrDefWithTravis(StashRelease.travisTag, versionName)
    return if (isTravisTag) {
        sdkVersionName
    } else {
        "$sdkVersionName-SNAPSHOT"
    }
}

fun Project.appVersionName(): String {
    val versionCode = propOrDefWithTravis(StashRelease.travisBuildNumber, StashRelease.versionCode)
    val versionName = StashRelease.versionName + "-$versionCode"

    return propOrDefWithTravis(StashRelease.travisTag, versionName)
}
