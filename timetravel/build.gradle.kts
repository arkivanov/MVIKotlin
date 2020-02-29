setupMultiplatform()
setupPublication()

doIfBuildTargetAvailable<BuildTarget.Android> {
    android {
        sourceSets {
            getByName("main") {
                res.srcDirs("src/androidMain/res")
            }
        }
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":core"))
                implementation(project(":core-internal"))
                implementation(project(":utils-internal"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":core-test-internal"))
            }
        }

        androidMain {
            dependencies {
                implementation(Deps.AndroidX.Core.Ktx)
                implementation(Deps.AndroidX.AppCompat.AppCompat)
                implementation(Deps.AndroidX.RecyclerView.RecyclerView)
                implementation(Deps.AndroidX.ConstraintLayout.ConstraintLayout)
            }
        }
    }
}
