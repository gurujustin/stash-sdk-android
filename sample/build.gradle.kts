/*
 * Copyright © MobiLab Solutions GmbH
 */

val haveFabricApiKey = propOrDefWithTravis(DemoRelease.fabricApiKey, "").isNotEmpty()

plugins {
    id("com.android.application")
    id("com.github.ben-manes.versions")
    id("CommonPlugin")
    id("androidx.navigation.safeargs.kotlin")
}
if (haveFabricApiKey) {
    apply(plugin = "io.fabric")
}

android {
    defaultConfig {
        applicationId = "com.mobilabsolutions.stash.sample"
        vectorDrawables.useSupportLibrary = true
        versionName = appVersionName()

        javaCompileOptions {
            annotationProcessorOptions {
                arguments = mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }

        manifestPlaceholders = mapOf("fabric-api-key" to propOrDefWithTravis(DemoRelease.fabricApiKey, ""))
    }

    signingConfigs {
        getByName("debug") {
            storeFile = rootProject.file("signing/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
            ext.set("alwaysUpdateBuildId", false)
            isCrunchPngs = false
            splits {
                density.isEnable = false
                abi.isEnable = false
            }
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    dataBinding {
        isEnabled = true
    }

    dexOptions {
        preDexLibraries = !isTravisBuild
    }

    lintOptions {
        disable("GradleDependency")
    }
}

dependencies {
    testImplementation(Libs.junit)

    androidTestImplementation(Libs.AndroidX.appcompat)
    androidTestImplementation(Libs.AndroidX.constraintlayout)

    implementation(project(Modules.stash))
    implementation(project(Modules.adyenIntegration))
    implementation(project(Modules.bsPayoneIntegration))
    implementation(project(Modules.braintreeIntegration))

    implementation(Libs.Kotlin.stdlib)

    implementation(Libs.AndroidX.constraintlayout)
    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.cardview)
    implementation(Libs.AndroidX.preference)

    implementation(Libs.Google.material)

    implementation(Libs.timber)
    implementation(Libs.Stetho.stetho)
    implementation(Libs.Stetho.okhttp3)

    implementation(Libs.Dagger.dagger)
    implementation(Libs.Dagger.daggerAndroid)
    implementation(Libs.Dagger.androidSupport)
    kapt(Libs.Dagger.compiler)
    kapt(Libs.Dagger.androidProcessor)

    implementation(Libs.RxJava.rxKotlin)
    implementation(Libs.RxJava.rxJava)
    implementation(Libs.RxJava.rxAndroid)

    implementation(Libs.OkHttp.okhttp)
    implementation(Libs.OkHttp.loggingInterceptor)

    implementation(Libs.Retrofit.retrofit)
    implementation(Libs.Retrofit.gsonConverter)
    implementation(Libs.Retrofit.retrofit_rxjava_adapter)

    implementation(Libs.AndroidX.Lifecycle.extensions)
    implementation(Libs.AndroidX.Lifecycle.viewmodel)
    implementation(Libs.AndroidX.Lifecycle.runtime)
    kapt(Libs.AndroidX.Lifecycle.compiler)

    implementation(Libs.Coroutines.core)
    implementation(Libs.Coroutines.rx2)
    implementation(Libs.Coroutines.android)

    compileOnly(Libs.AssistedInject.annotationDagger2)
    kapt(Libs.AssistedInject.processorDagger2)

    implementation(Libs.mvrx)

    implementation(Libs.Epoxy.epoxy)
    implementation(Libs.Epoxy.dataBinding)
    kapt(Libs.Epoxy.processor)

    implementation(Libs.AndroidX.Navigation.fragment)
    implementation(Libs.AndroidX.Navigation.ui)

    implementation(Libs.AndroidX.Room.common)
    implementation(Libs.AndroidX.Room.runtime)
    implementation(Libs.AndroidX.Room.rxjava2)
    implementation(Libs.AndroidX.Room.ktx)
    kapt(Libs.AndroidX.Room.compiler)

    implementation(Libs.Epoxy.epoxy)
    implementation(Libs.Epoxy.dataBinding)
    kapt(Libs.Epoxy.processor)

    implementation(Libs.Google.crashlytics)
}
