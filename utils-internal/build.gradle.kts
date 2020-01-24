project.setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }

        jvmMain {
            dependencies {
                implementation(Deps.Json.Json)
            }
        }
    }
}
