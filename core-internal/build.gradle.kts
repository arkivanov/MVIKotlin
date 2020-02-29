setupMultiplatform()
setupPublication()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":utils-internal"))
            }
        }
    }
}
