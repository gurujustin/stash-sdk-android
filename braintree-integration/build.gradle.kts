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
    implementation(project(Modules.stash))
    implementation(Libs.Kotlin.stdlib)

    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.swipeRefreshLayout)
    implementation(Libs.AndroidX.constraintlayout)
    implementation(Libs.Google.material)

    implementation(Libs.braintree)

    implementation(Libs.Dagger.dagger)
    kapt(Libs.Dagger.compiler)

    testImplementation(Libs.junit)
    kaptTest(Libs.Dagger.compiler)

    androidTestImplementation(Libs.AndroidX.appcompat)
    androidTestImplementation(Libs.AndroidX.constraintlayout)

    androidTestImplementation(Libs.AndroidX.Test.runner)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    androidTestImplementation(Libs.AndroidX.Test.espressoIntents)
    androidTestImplementation(Libs.AndroidX.Test.rules)
    androidTestImplementation(Libs.AndroidX.Test.uiAutomator)
    kaptAndroidTest(Libs.Dagger.compiler)
}

tasks {
    create<DokkaAndroidTask>("dokkaPublic") {
        moduleName = "braintree-integration"
        outputFormat = "html"
        outputDirectory = "$buildDir/dokkaPublic"
        packageOptions {
            prefix = "com.mobilabsolutions.stash.internal"
            suppress = true
        }
    }

    dokka {
        moduleName = "braintree-integration"
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
    }

    val dokkaJavadoc = create<DokkaAndroidTask>("dokkaJavadoc") {
        moduleName = "braintree-integration"
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/dokkaJavadoc"
        packageOptions {
            prefix = "com.mobilabsolutions.stash.internal"
            suppress = true
        }
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
        create<MavenPublication>("braintree") {
            groupId = "com.mobilabsolutions.stash"
            artifactId = "braintree"
            version = android.defaultConfig.versionName

            artifact("$buildDir/outputs/aar/braintree-integration-release.aar")
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
                name.set("Stash - Braintree")
                description.set("The Braintree Integration for the Stash SDK")
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
                        if (it.group == "payment-sdk-android-open") { // Core lib
                            with(dependenciesNode.appendNode("dependency")) {
                                appendNode("groupId", "com.mobilabsolutions.stash")
                                appendNode("artifactId", "core")
                                appendNode("version", android.defaultConfig.versionName)
                                appendNode("scope", "runtime")
                            }
                        } else if (it.group != null && it.name != "unspecified" && it.version != null) {
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
    sign(publishing.publications["braintree"])
}