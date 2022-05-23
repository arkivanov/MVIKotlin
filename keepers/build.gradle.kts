import com.arkivanov.gradle.bundle
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
        val android by bundle()

        common.main.dependencies {
            implementation(project(":utils-internal"))
        }

        android.main.dependencies {
            implementation(deps.androidx.lifecycle.lifecycleViewmodel)
            implementation(deps.androidx.lifecycle.lifecycleViewmodelSavedstate)
        }

        android.test.dependencies {
            implementation(deps.androidx.lifecycle.lifecycleRuntime)
        }
    }
}
