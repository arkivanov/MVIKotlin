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
    namespace = "com.arkivanov.mvikotlin.logging"
}

kotlin {
    setupSourceSets {
        common.main.dependencies {
            implementation(project(":mvikotlin"))
        }

        common.test.dependencies {
            implementation(project(":mvikotlin-test-internal"))
        }

        all {
            languageSettings {
                optIn("com.arkivanov.mvikotlin.core.utils.internal.InternalMviKotlinApi")
            }
        }
    }
}
