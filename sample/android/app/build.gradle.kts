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

    packagingOptions {
        exclude("META-INF/reaktive_debug.kotlin_module")
    }
}

dependencies {
    implementation(Deps.Jetbrains.Kotlin.StdLib.Jdk7)
    implementation(Deps.AndroidX.AppCompat.AppCompat)
    implementation(Deps.AndroidX.RecyclerView.RecyclerView)
    implementation(Deps.AndroidX.ConstraintLayout.ConstraintLayout)
    implementation(Deps.AndroidX.DrawerLayout.DrawerLayout)
    implementation(project(":mvikotlin"))
    debugImplementation(project(":timetravel"))
    debugImplementation(project(":logging"))
    implementation(project(":sample:shared:reaktive"))
}
