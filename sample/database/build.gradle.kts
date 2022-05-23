import com.arkivanov.gradle.bundle
import com.arkivanov.gradle.dependsOn
import com.arkivanov.gradle.iosCompat
import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupSourceSets

plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    android()
    js(IR) { browser() }
    iosCompat(
        arm64 = null, // Comment out to enable arm64 target
    )
}

kotlin {
    setupSourceSets {
        val darwin by bundle()

        darwin dependsOn common
        darwinSet dependsOn darwin

        common.main.dependencies {
            implementation(project(":utils-internal"))
            implementation(project(":mvikotlin"))
        }
    }
}
