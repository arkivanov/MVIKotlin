import io.gitlab.arturbosch.detekt.detekt

buildscript {
    repositories {
        gradlePluginPortal()
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
