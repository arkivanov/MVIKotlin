setupMultiplatform()
setupPublication()

androidCompat {
    sourceSets {
        getByName("main") {
            res.srcDirs("src/androidMain/res")
        }
    }
}

kotlinCompat {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":rx"))
                implementation(project(":rx-internal"))
                implementation(project(":utils-internal"))
                implementation(project(":mvikotlin-timetravel-proto-internal"))
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
