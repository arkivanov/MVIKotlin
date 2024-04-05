import com.arkivanov.gradle.bundle
import com.arkivanov.gradle.dependsOn
import com.arkivanov.gradle.setupBinaryCompatibilityValidator
import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupPublication
import com.arkivanov.gradle.setupSourceSets

plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform()
setupPublication()
setupBinaryCompatibilityValidator()

android {
    namespace = "com.arkivanov.mvikotlin.core"
}

kotlin {
    setupSourceSets {
        val android by bundle()
        val js by bundle()
        val wasmJs by bundle()
        val web by bundle()
        val webNative by bundle()
        val java by bundle()
        val native by bundle()
        val darwin by bundle()

        webNative dependsOn common
        native dependsOn webNative
        darwin dependsOn native
        java dependsOn common
        web dependsOn webNative
        js dependsOn web
        wasmJs dependsOn web

        javaSet dependsOn java
        (nativeSet - darwinSet) dependsOn native
        darwinSet dependsOn darwin
        javaSet dependsOn java

        common.main.dependencies {
            api(deps.essenty.lifecycle)
            api(deps.essenty.instanceKeeper)
        }

        common.test.dependencies {
            implementation(deps.reaktive.reaktive)
        }

        all {
            languageSettings {
                optIn("com.arkivanov.mvikotlin.core.utils.internal.InternalMviKotlinApi")
            }
        }
    }
}
