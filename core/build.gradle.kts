setupMultiplatform()
setupPublication()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":utils-internal"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }
    }
}
