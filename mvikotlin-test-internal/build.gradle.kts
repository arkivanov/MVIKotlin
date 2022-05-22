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
            implementation(project(":rx"))
            implementation(project(":rx-internal"))
            implementation(project(":utils-internal"))
            implementation(deps.kotlin.kotlinTestCommon)
            implementation(deps.kotlin.kotlinTestAnnotationsCommon)
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
    }
}
