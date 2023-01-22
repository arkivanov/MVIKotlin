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
            implementation(project(":utils-internal"))
        }

        common.test.dependencies {
            implementation(project(":mvikotlin-test-internal"))
            implementation(project(":rx"))
        }
    }
}
