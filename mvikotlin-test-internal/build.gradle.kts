import com.arkivanov.gradle.bundle
import com.arkivanov.gradle.dependsOn
import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupSourceSets

plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform()

android {
    namespace = "com.arkivanov.mvikotlin.core.test.internal"
}

kotlin {
    setupSourceSets {
        val android by bundle()
        val jvm by bundle()
        val js by bundle()
        val darwin by bundle()

        darwin dependsOn common
        darwinSet dependsOn darwin

        common.main.dependencies {
            implementation(project(":mvikotlin"))
            implementation(deps.kotlin.kotlinTestCommon)
            implementation(deps.kotlin.kotlinTestAnnotationsCommon)
            implementation(deps.reaktive.reaktive)
        }

        js.main.dependencies {
            implementation(deps.kotlin.kotlinTestJs)
        }

        android.main.dependencies {
            implementation(deps.kotlin.kotlinTestJunit)
        }

        jvm.main.dependencies {
            implementation(deps.kotlin.kotlinTestJunit)
        }

        all {
            languageSettings {
                optIn("com.arkivanov.mvikotlin.core.utils.internal.InternalMviKotlinApi")
            }
        }
    }
}
