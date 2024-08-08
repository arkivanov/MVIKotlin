import com.arkivanov.gradle.AndroidConfig
import com.arkivanov.gradle.BinaryCompatibilityValidatorConfig
import com.arkivanov.gradle.PublicationConfig
import com.arkivanov.gradle.ensureUnreachableTasksDisabled
import com.arkivanov.gradle.iosCompat
import com.arkivanov.gradle.macosCompat
import com.arkivanov.gradle.setupDefaults
import com.arkivanov.gradle.setupDetekt
import com.arkivanov.gradle.tvosCompat
import com.arkivanov.gradle.watchosCompat

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    dependencies {
        classpath(deps.kotlin.kotlinGradlePlug)
        classpath(deps.android.gradle)
        classpath(deps.intellij.gradleIntellijPlug)
        classpath(deps.compose.composeGradlePlug)
        classpath(deps.kotlin.composeCompilerGradlePlug)
        classpath(deps.kotlinx.binaryCompatibilityValidator)
        classpath(deps.detekt.gradleDetektPlug)
    }
}

plugins {
    id("com.arkivanov.gradle.setup")
}

setupDefaults(
    multiplatformConfigurator = {
        androidTarget()
        jvm()
        js { browser() }
        wasmJs { browser() }
        linuxX64()
        iosCompat()
        watchosCompat()
        tvosCompat()
        macosCompat()
    },
    androidConfig = AndroidConfig(
        minSdkVersion = 15,
        compileSdkVersion = 34,
        targetSdkVersion = 34,
    ),
    binaryCompatibilityValidatorConfig = BinaryCompatibilityValidatorConfig(
        nonPublicMarkers = listOf("com.arkivanov.mvikotlin.core.utils.internal.InternalMviKotlinApi"),
    ),
    publicationConfig = PublicationConfig(
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
        repositoryUserName = System.getenv("SONATYPE_USER_NAME"),
        repositoryPassword = System.getenv("SONATYPE_PASSWORD"),
    ),
)

setupDetekt()
ensureUnreachableTasksDisabled()

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
