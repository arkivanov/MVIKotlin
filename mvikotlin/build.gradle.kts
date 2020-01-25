project.setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":core"))
                implementation(project(":core-internal"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":utils-internal"))
                implementation(project(":core-test-internal"))
            }
        }
    }
}
