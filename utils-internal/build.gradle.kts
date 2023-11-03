import com.arkivanov.gradle.bundle
import com.arkivanov.gradle.dependsOn
import com.arkivanov.gradle.plus
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

android {
    namespace = "com.arkivanov.mvikotlin.utils.internal"
}

kotlin {
    setupSourceSets {
        val js by bundle()
        val native by bundle()
        val darwin by bundle()
        val java by bundle()

        (java + native) dependsOn common
        darwin dependsOn native
        javaSet dependsOn java
        (nativeSet - darwinSet) dependsOn native
        darwinSet dependsOn darwin
    }
}
