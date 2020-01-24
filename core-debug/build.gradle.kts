project.setupMultiplatform()

android {
    sourceSets {
        getByName("main") {
            res.srcDirs("src/androidMain/res")
        }
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                implementation(project(":core-internal"))
                implementation(project(":utils-internal"))
                implementation(Deps.Badoo.Reaktive.Utils)
                implementation(Deps.AndroidX.Core.Ktx)
                implementation(Deps.AndroidX.AppCompat.AppCompat)
                implementation(Deps.AndroidX.RecyclerView.RecyclerView)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":core-test-internal"))
            }
        }

        jvmMain {
            dependencies {
                implementation(Deps.Json.Json)
            }
        }
    }
}
