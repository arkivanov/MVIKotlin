plugins {
    id("com.android.application")
    kotlin("android")
}

setupAndroidSdkVersions()

androidCompat {
    defaultConfig {
        applicationId = "com.arkivanov.rxkotlin.sample.todo.android"
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
        exclude("META-INF/kotlinx-coroutines-core.kotlin_module")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(Deps.Jetbrains.Kotlin.StdLib.Jdk7)
    implementation(Deps.AndroidX.AppCompat.AppCompat)
    implementation(Deps.AndroidX.RecyclerView.RecyclerView)
    implementation(Deps.AndroidX.ConstraintLayout.ConstraintLayout)
    implementation(Deps.AndroidX.DrawerLayout.DrawerLayout)
    implementation(Deps.AndroidX.Core.Ktx)
    implementation(Deps.AndroidX.Lifecycle.LifecycleCommonJava8)
    implementation(Deps.Jetbrains.Kotlinx.Coroutines.Core)
    implementation(Deps.Jetbrains.Kotlinx.Coroutines.Android)
    implementation(project(":mvikotlin"))
    implementation(project(":mvikotlin-main"))
    implementation(project(":mvikotlin-timetravel"))
    implementation(project(":mvikotlin-logging"))
    implementation(project(":mvikotlin-extensions-androidx"))
    implementation(project(":keepers"))
    implementation(project(":sample:todo-reaktive"))
    implementation(project(":sample:todo-coroutines"))
}
