/*
 * Copyright © MobiLab Solutions GmbH
 */

import org.jetbrains.dokka.gradle.DokkaAndroidTask

plugins {
    id("com.android.library")
    id("org.jetbrains.dokka-android")
    id("maven-publish")
    signing
    id("CommonPlugin")
}

dependencies {
    implementation(Libs.Kotlin.stdlib)

    implementation(Libs.AndroidX.constraintlayout)
    implementation(Libs.AndroidX.appcompat)

    api(Libs.OkHttp.okhttp)
    api(Libs.OkHttp.loggingInterceptor)

    api(Libs.Retrofit.retrofit)
    api(Libs.Retrofit.retrofit_rxjava_adapter)
    api(Libs.Retrofit.gsonConverter)

    api(Libs.timber)

    api(Libs.RxJava.rxJava)
    api(Libs.RxJava.rxAndroid)
    api(Libs.RxJava.rxKotlin)

    api(Libs.threetenabp)

    implementation(Libs.AndroidX.recyclerview)
    implementation(Libs.Google.material)

    implementation(Libs.Utils.commonsValidator)

    implementation(Libs.Dagger.dagger)
    implementation(Libs.Dagger.daggerAndroid)
    implementation(Libs.Dagger.androidSupport)
    kapt(Libs.Dagger.compiler)

    implementation(Libs.caligraphy)
    implementation(Libs.viewPump)

    api(Libs.threetenabp)
    implementation(Libs.iban4j)

    implementation(Libs.Stetho.stetho)
    implementation(Libs.Stetho.okhttp3)

    testImplementation(project(Modules.bsPayoneIntegration))
    testImplementation(project(Modules.adyenIntegration))
    testImplementation(project(Modules.braintreeIntegration))

    testImplementation(Libs.Kotlin.test)

    testImplementation(Libs.junit)
    testImplementation(Libs.mockitoCore)
    testImplementation(Libs.OkHttp.mockwebserver)
    testImplementation(Libs.PowerMock.module)
    testImplementation(Libs.PowerMock.api)
    kaptTest(Libs.Dagger.compiler)

    androidTestImplementation(Libs.OkHttp.mockwebserver)
    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.core)
    androidTestImplementation(Libs.AndroidX.Test.coreKtx)
    androidTestImplementation(Libs.AndroidX.Test.ext)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    kaptAndroidTest(Libs.Dagger.compiler)
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(0, TimeUnit.SECONDS)
        cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
    }
}

tasks {
    create<DokkaAndroidTask>("dokkaPublic") {
        moduleName = "lib"
        outputFormat = "html"
        outputDirectory = "$buildDir/dokkaPublic"
        packageOptions {
            prefix = "com.mobilabsolutions.stash.internal"
            suppress = true
        }
        includes = listOf(
            "src/main/java/com/mobilabsolutions/stash/core/model/model-package-description.md",
            "src/main/java/com/mobilabsolutions/stash/core/payment-sdk-package-description.md"
        )
    }

    dokka {
        moduleName = "lib"
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
    }

    val dokkaJavadoc = create<DokkaAndroidTask>("dokkaJavadoc") {
        moduleName = "lib"
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/dokkaJavadoc"
        packageOptions {
            prefix = "com.mobilabsolutions.stash.internal"
            suppress = true
        }
        includes = listOf(
            "src/main/java/com/mobilabsolutions/stash/core/model/model-package-description.md",
            "src/main/java/com/mobilabsolutions/stash/core/payment-sdk-package-description.md"
        )
    }

    create<Jar>("javadocJar") {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from("$buildDir/dokkaJavadoc")
    }

    create<Jar>("sourcesJar") {
        from(android.sourceSets["main"].java.srcDirs)
        archiveClassifier.set("sources")
    }

    publish {
        dependsOn(build)
    }

    publishToMavenLocal {
        dependsOn(build)
    }
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            groupId = "com.mobilabsolutions.stash"
            artifactId = "core"
            version = android.defaultConfig.versionName

            artifact("$buildDir/outputs/aar/lib-release.aar")
            artifact(tasks["javadocJar"])
            artifact(tasks["sourcesJar"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name.set("Stash")
                description.set("The Stash SDK simplifies the integration of the payment process into your mobile application and abstracts away a lot of the internal complexity that different payment service providers' solutions have.")
                url.set("https://mobilabsolutions.com/")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("Ugi")
                        name.set("Uglješa Jovanović")
                        email.set("ugi@mobilabsolutions.com")
                    }
                    developer {
                        id.set("Yisuk")
                        name.set("Yisuk Kim")
                        email.set("yisuk@mobilabsolutions.com")
                    }
                    developer {
                        id.set("Biju")
                        name.set("Biju Parvathy")
                        email.set("biju@mobilabsolutions.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/mobilabsolutions/payment-sdk-android-open.git")
                    developerConnection.set("scm:git:https://github.com/mobilabsolutions/payment-sdk-android-open.git")
                    url.set("https://github.com/mobilabsolutions/payment-sdk-android-open")
                }
                withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")
                    // List all "implementation" dependencies (for new Gradle) as "runtime" dependencies
                    configurations.implementation.get().allDependencies.forEach {
                        if (it.group != null && it.name != "unspecified" && it.version != null) {
                            with(dependenciesNode.appendNode("dependency")) {
                                appendNode("groupId", it.group)
                                appendNode("artifactId", it.name)
                                appendNode("version", it.version)
                                appendNode("scope", "runtime")
                            }
                        }
                    }
                    // List all "api" dependencies as "compile" dependencies
                    configurations.api.get().allDependencies.forEach {
                        if (it.group != null && it.name != "unspecified" && it.version != null) {
                            with(dependenciesNode.appendNode("dependency")) {
                                appendNode("groupId", it.group)
                                appendNode("artifactId", it.name)
                                appendNode("version", it.version)
                                appendNode("scope", "compile")
                            }
                        }
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "nexus"

            val releasesRepoUrl = "https://nexus.mblb.net/repository/releases/"
            val snapshotsRepoUrl = "https://nexus.mblb.net/repository/snapshots/"

            // It's a release if tagged, else snapshot
            url = uri(if (isTravisTag) releasesRepoUrl else snapshotsRepoUrl)

            credentials {
                username = propOrDefWithTravis(StashRelease.MobilabNexusUsername, "")
                password = propOrDefWithTravis(StashRelease.MobilabNexusPassword, "")
            }
        }
    }
}

signing {
    sign(publishing.publications["lib"])
}
