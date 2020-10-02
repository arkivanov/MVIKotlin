setupMultiplatform()
setupPublication()

kotlinCompat {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":utils-internal"))
            }
        }

        commonTest {
            dependencies {
                implementation(project(":mvikotlin-test-internal"))
                implementation(project(":rx"))
            }
        }
    }
}
