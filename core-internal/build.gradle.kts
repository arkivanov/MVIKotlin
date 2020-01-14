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
                implementation(project(":core"))

                implementation(Deps.Jetbrains.Kotlin.StdLib.Common)
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }

        getByName("androidMain") {
            dependencies {
                implementation(Deps.Jetbrains.Kotlin.StdLib.Jdk7)
            }
        }
    }
}
