setupMultiplatform()
setupPublication()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }
    }
}