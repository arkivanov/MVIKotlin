setupMultiplatform()
setupPublication()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":utils-internal"))
                implementation(project(":rx"))
                implementation(project(":rx-internal"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }
    }
}
