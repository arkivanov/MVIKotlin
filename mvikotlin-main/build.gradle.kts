setupMultiplatform()
setupPublication()

kotlinCompat {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":rx"))
                implementation(project(":rx-internal"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":mvikotlin-test-internal"))
            }
        }
    }
}
