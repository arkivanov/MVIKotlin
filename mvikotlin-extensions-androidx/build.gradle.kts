buildTargets = setOf(BuildTarget.Android)

setupMultiplatform()
setupPublication()

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(Deps.AndroidX.AppCompat.AppCompat)
                implementation(Deps.AndroidX.Lifecycle.LifecycleCommonJava8)
                implementation(Deps.AndroidX.Lifecycle.LifecycleViewModel)
            }
        }
    }
}
