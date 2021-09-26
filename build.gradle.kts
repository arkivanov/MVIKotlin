import com.arkivanov.gradle.PublicationConfig
import io.gitlab.arturbosch.detekt.detekt

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    dependencies {
        @Suppress("UnstableApiUsage")
        val deps = project.extensions.getByType<VersionCatalogsExtension>().named("deps") as org.gradle.accessors.dm.LibrariesForDeps

        classpath(deps.kotlin.kotlinGradlePlug)
        classpath(deps.intellij.gradleIntellijPlug)
        classpath(deps.compose.composeGradlePlug)
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt").version("1.14.2")
    id("com.arkivanov.gradle.setup")
}

publicationDefaults {
    config =
        PublicationConfig(
            group = "com.arkivanov.mvikotlin",
            version = deps.versions.mvikotlin.get(),
            projectName = "MVIKotlin",
            projectDescription = "Kotlin Multiplatform MVI framework",
            projectUrl = "https://github.com/arkivanov/MVIKotlin",
            scmUrl = "scm:git:git://github.com/arkivanov/MVIKotlin.git",
            licenseName = "The Apache License, Version 2.0",
            licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0.txt",
            developerId = "arkivanov",
            developerName = "Arkadii Ivanov",
            developerEmail = "arkann1985@gmail.com",
            signingKey = System.getenv("SIGNING_KEY"),
            signingPassword = System.getenv("SIGNING_PASSWORD"),
            repositoryUrl = "https://oss.sonatype.org/service/local/staging/deployByRepositoryId/${System.getenv("SONATYPE_REPOSITORY_ID")}",
            repositoryUserName = "arkivanov",
            repositoryPassword = System.getenv("SONATYPE_PASSWORD"),
        )
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins.apply("io.gitlab.arturbosch.detekt")

    detekt {
        toolVersion = "1.14.2"
        parallel = true
        config = files("$rootDir/detekt.yml")
        input = files(file("src").listFiles()?.find { it.isDirectory } ?: emptyArray<Any>())
    }

    // Workaround until Detekt is updated: https://github.com/detekt/detekt/issues/3840.
    // The current version depends on kotlinx-html-jvm:0.7.2 which is not in Maven Central.
    configurations.named("detekt") {
        resolutionStrategy {
            force("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
        }
    }
}
