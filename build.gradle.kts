import com.arkivanov.gradle.AndroidConfig
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
        classpath(deps.kotlinx.binaryCompatibilityValidator)
        classpath(deps.detekt.gradleDetektPlug)
    }
}

plugins {
    id("com.arkivanov.gradle.setup")
}

setupDefaults(
    multiplatformConfigurator = {
        android()
        jvm()
        js(BOTH) { browser() }
        linuxX64()
        iosCompat()
        watchosCompat()
        tvosCompat()
        macosCompat()
    },
    androidConfig = AndroidConfig(
        minSdkVersion = 15,
        compileSdkVersion = 31,
        targetSdkVersion = 31,
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
        repositoryUserName = "arkivanov",
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

    afterEvaluate {
        // Workaround for https://youtrack.jetbrains.com/issue/KT-52776
        rootProject.extensions.findByType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>()?.apply {
            versions.webpackCli.version = "4.10.0"
        }
    }
}
