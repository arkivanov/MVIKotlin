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
    namespace = "com.arkivanov.mvikotlin.timetravel"
}

kotlin {
    setupSourceSets {
        val android by bundle()
        val darwin by bundle()
        val java by bundle()

        darwin dependsOn common
        java dependsOn common
        javaSet dependsOn java
        darwinSet dependsOn darwin

        common.main.dependencies {
            implementation(project(":mvikotlin"))
            implementation(project(":mvikotlin-timetravel-proto-internal"))
            implementation(deps.kotlinx.kotlinxSerializationJson)
        }

        common.test.dependencies {
            implementation(project(":mvikotlin-test-internal"))
        }

        android.main.dependencies {
            implementation(deps.androidx.core.coreKtx)
            implementation(deps.androidx.appcompat.appcompat)
            implementation(deps.androidx.recyclerview.recyclerview)
            implementation(deps.androidx.constraintlayout.constraintlayout)
        }

        all {
            languageSettings {
                optIn("com.arkivanov.mvikotlin.core.utils.internal.InternalMviKotlinApi")
            }
        }
    }
}
