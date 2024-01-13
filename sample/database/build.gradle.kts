import com.arkivanov.gradle.iosCompat
import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupSourceSets

plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
    id("kotlinx-serialization")
}

setupMultiplatform {
    androidTarget()
    js { browser() }
    iosCompat(
        arm64 = null, // Comment out to enable arm64 target
    )
}

android {
    namespace = "com.arkivanov.mvikotlin.sample.database"
}

kotlin {
    setupSourceSets {
        common.main.dependencies {
            implementation(project(":mvikotlin"))
            implementation(deps.kotlinx.kotlinxSerializationCore)
        }
    }
}
