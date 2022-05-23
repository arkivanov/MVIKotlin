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

kotlin {
    setupSourceSets {
        val js by bundle()
        val jvmJs by bundle()
        val native by bundle()
        val darwin by bundle()
        val java by bundle()

        (jvmJs + native) dependsOn common
        darwin dependsOn native
        (js + java) dependsOn jvmJs
        javaSet dependsOn java
        linuxSet dependsOn native
        darwinSet dependsOn darwin
    }
}
