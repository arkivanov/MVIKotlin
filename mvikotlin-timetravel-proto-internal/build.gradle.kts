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
    namespace = "com.arkivanov.mvikotlin.timetravel.proto"
}

kotlin {
    setupSourceSets {
        val nonJs by bundle()
        val java by bundle()
        val native by bundle()
        val wasmJs by bundle()

        nonJs dependsOn common
        (java + native + wasmJs) dependsOn nonJs
        javaSet dependsOn java
        nativeSet dependsOn native

        common.main.dependencies {
            implementation(deps.kotlinx.kotlinxSerializationJson)
        }
    }
}
