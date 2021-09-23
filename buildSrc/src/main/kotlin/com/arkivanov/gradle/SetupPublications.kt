package com.arkivanov.gradle

import org.gradle.accessors.dm.LibrariesForDeps
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun Project.setupMultiplatformPublications() {
    plugins.apply("maven-publish")

    @Suppress("UnstableApiUsage")
    val deps = project.extensions.getByType<VersionCatalogsExtension>().named("deps") as LibrariesForDeps

    group = "com.arkivanov.mvikotlin"
    version = deps.versions.mvikotlin.get()

    publishing {
        publications.withType<MavenPublication>().forEach {
            setupPublicationPom(it)
        }
    }

    setupPublicationRepository()

    doIfTargetEnabled<Target.Android> {
        kotlin {
            android {
                publishLibraryVariants("release", "debug")
            }
        }
    }

    enablePublicationTasks()
}

private fun Project.enablePublicationTasks() {
    val isMetadataOnly = System.getProperty("publish_metadata") != null
    val targets = extensions.getByType<KotlinMultiplatformExtension>().targets

    tasks.withType<AbstractPublishToMaven>().configureEach {
        val publicationName = publication?.name

        enabled =
            when {
                publicationName == "kotlinMultiplatform" -> isMetadataOnly

                publicationName != null -> {
                    val target = targets.find { it.name.startsWith(publicationName) }
                    checkNotNull(target) { "Target not found for publication $publicationName" }
                    !isMetadataOnly && target.isCompilationAllowed
                }

                else -> {
                    val target = targets.find { name.contains(other = it.name, ignoreCase = true) }
                    checkNotNull(target) { "Target not found for publication $this" }
                    !isMetadataOnly && target.isCompilationAllowed
                }
            }

        println("Publication $this enabled=$enabled")
    }
}

fun Project.setupPublicationPom(publication: MavenPublication) {
    var javadocJar: Task? = tasks.findByName("javadocJar")
    if (javadocJar == null) {
        javadocJar =
            tasks.create<Jar>("javadocJar").apply {
                archiveClassifier.set("javadoc")
            }
    }

    publication.apply {
        artifact(javadocJar)

        pom {
            name.set("MVIKotlin")
            description.set("Kotlin Multiplatform MVI framework")
            url.set("https://github.com/arkivanov/MVIKotlin")

            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }

            developers {
                developer {
                    id.set("arkivanov")
                    name.set("Arkadii Ivanov")
                    email.set("arkann1985@gmail.com")
                }
            }

            scm {
                url.set("https://github.com/arkivanov/MVIKotlin")
                connection.set("scm:git:git://github.com/arkivanov/MVIKotlin.git")
                developerConnection.set("scm:git:git://github.com/arkivanov/MVIKotlin.git")
            }
        }
    }
}

private fun Project.setupPublicationRepository() {
    val isSigningEnabled = System.getenv("SIGNING_KEY") != null

    if (isSigningEnabled) {
        plugins.apply("signing")
    }

    publishing {
        if (isSigningEnabled) {
            withExtension<SigningExtension> {
                useInMemoryPgpKeys(System.getenv("SIGNING_KEY"), System.getenv("SIGNING_PASSWORD"))
                sign(publications)
            }
        }

        repositories {
            maven {
                setUrl("https://oss.sonatype.org/service/local/staging/deployByRepositoryId/${System.getenv("SONATYPE_REPOSITORY_ID")}")

                credentials {
                    username = "arkivanov"
                    password = System.getenv("SONATYPE_PASSWORD")
                }
            }
        }
    }
}

private fun Project.publishing(block: PublishingExtension.() -> Unit) {
    withExtension(block)
}
