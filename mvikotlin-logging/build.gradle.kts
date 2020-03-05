setupMultiplatform()
setupPublication()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":utils-internal"))
                implementation(Deps.Jetbrains.Kotlin.Reflect)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":mvikotlin-test-internal"))
                implementation(project(":rx"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }
    }
}
