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

kotlin {
    setupSourceSets {
        val darwin by bundle()

        darwin dependsOn common
        darwinSet dependsOn darwin

        common.main.dependencies {
            implementation(project(":mvikotlin"))
            implementation(project(":rx"))
            implementation(project(":rx-internal"))
            implementation(project(":utils-internal"))
        }

        common.test.dependencies {
            implementation(project(":mvikotlin-test-internal"))
        }
    }
}
