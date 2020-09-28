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
                implementation(Deps.Badoo.Reaktive.Utils)

            }
        }

        androidMain {
            dependencies {
                implementation(Deps.AndroidX.Lifecycle.LifecycleViewModel)
            }
        }

        androidTest {
            dependencies {
                implementation(Deps.AndroidX.Lifecycle.LifecycleRuntime)
            }
        }
    }
}
