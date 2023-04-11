/*
 * Copyright © MobiLab Solutions GmbH
 */

plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    jcenter()
    google()
}

dependencies {
    implementation("com.android.tools.build:gradle:3.5.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.50")
    implementation("com.jaredsburrows:gradle-license-plugin:0.8.42")
    implementation("org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.18")
}

gradlePlugin {
    plugins {
        register("CommonPlugin") {
            id = "CommonPlugin"
            implementationClass = "CommonPlugin"
        }
    }
}