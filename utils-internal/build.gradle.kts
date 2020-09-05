setupMultiplatform()
setupPublication()

kotlinCompat {
    sourceSets {
        commonMain {
            dependencies {
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }
    }
}
