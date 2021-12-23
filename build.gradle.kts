import com.arkivanov.gradle.AndroidConfig
import com.arkivanov.gradle.PublicationConfig
import com.arkivanov.gradle.Target
import com.arkivanov.gradle.darwinSet
import com.arkivanov.gradle.javaSet
import com.arkivanov.gradle.named

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
        classpath(deps.android.gradle)
        classpath(deps.intellij.gradleIntellijPlug)
        classpath(deps.compose.composeGradlePlug)
        classpath(deps.kotlinx.binaryCompatibilityValidatorGradlePlug)
        classpath(deps.detekt.gradleDetektPlug)
    }
}

plugins {
    id("com.arkivanov.gradle.setup")
}

setupAllProjects {
    multiplatformTargets(
        Target.Android,
        Target.Jvm,
        Target.Js(),
        Target.Linux,
        Target.Ios(),
        Target.WatchOs(),
        Target.MacOs(),
    )

    multiplatformSourceSets {
        val jvmJs by named(common)
        val jvmNative by named(common)
        val jsNative by named(common)
        val native by named(jvmNative, jsNative)
        val darwin by named(native)
        val java by named(jvmJs, jvmNative)

        js.dependsOn(jvmJs, jsNative)
        javaSet.dependsOn(java)
        linuxX64.dependsOn(native)
        darwinSet.dependsOn(darwin)
    }

    androidConfig(
        AndroidConfig(
            minSdkVersion = 15,
            compileSdkVersion = 31,
            targetSdkVersion = 31,
        )
    )

    publicationConfig(
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
    )

    detekt()
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
