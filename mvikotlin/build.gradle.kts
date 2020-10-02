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
        commonMain {
            dependencies {
                implementation(project(":utils-internal"))
                implementation(project(":rx"))
                implementation(project(":rx-internal"))
                implementation(project(":keepers"))
            }
        }

        androidMain {
            dependencies {
                implementation(Deps.AndroidX.Lifecycle.LifecycleCommonJava8)
                implementation(Deps.AndroidX.Lifecycle.LifecycleRuntime)
            }
        }
    }
}
