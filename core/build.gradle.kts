plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

android {
    compileSdkVersion(29)
}

kotlin {
    android()
    //    jvm("jvm")

    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(Deps.Jetbrains.Kotlin.StdLib.Common)
            }
        }

        getByName("androidMain") {
            dependencies {
                implementation(Deps.Jetbrains.Kotlin.StdLib.Jdk7)
            }
        }
    }
}
