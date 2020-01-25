plugins {
    id("com.android.application")
    kotlin("android")
}

project.setupAndroidSdkVersions()

android {
    defaultConfig {
        applicationId = "com.arkivanov.mvikotlin.android.sample"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(Deps.Jetbrains.Kotlin.StdLib.Jdk7)
}
