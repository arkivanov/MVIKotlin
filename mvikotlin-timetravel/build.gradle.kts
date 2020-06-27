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
                implementation(project(":mvikotlin"))
                implementation(project(":rx"))
                implementation(project(":rx-internal"))
                implementation(project(":utils-internal"))
                implementation(project(":mvikotlin-timetravel-proto-internal"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":mvikotlin-test-internal"))
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
