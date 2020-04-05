plugins {
    id("com.android.library")
    id("kotlin-android")
}

setupAndroidSdkVersions()
setupPublication()

afterEvaluate {
    extensions.getByType<PublishingExtension>().run {
        publications.create<MavenPublication>("all") {
            from(components["all"])
        }
    }
}

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
