project.setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                implementation(project(":utils-internal"))
                implementation(Deps.Jetbrains.Kotlin.Reflect)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":core-test-internal"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }
    }
}
