plugins {
    id("com.android.library")
    id("kotlin-android")
}

setupAndroidSdkVersions()
setupPublication()

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":mvikotlin"))
    implementation(Deps.Jetbrains.Kotlin.StdLib.Jdk7)
    implementation(Deps.AndroidX.Lifecycle.LifecycleCommonJava8)
}
