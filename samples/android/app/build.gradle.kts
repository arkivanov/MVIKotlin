plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        applicationId = "com.arkivanov.mvikotlin.android.sample"
        minSdkVersion(15)
        targetSdkVersion(29)
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
    implementation(project(":core"))
    implementation(project(":core-main"))
}
