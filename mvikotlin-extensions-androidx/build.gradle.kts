buildTargets = setOf(BuildTarget.Android)

setupMultiplatform()
setupPublication()

androidCompat {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

kotlinCompat {
    sourceSets {
        androidMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(Deps.AndroidX.Lifecycle.LifecycleCommonJava8)
                implementation(Deps.AndroidX.Lifecycle.LifecycleViewModel)
                implementation(Deps.AndroidX.Lifecycle.LifecycleViewModelSavedState)
                implementation(Deps.AndroidX.Lifecycle.LifecycleRuntime)
            }
        }
    }
}
