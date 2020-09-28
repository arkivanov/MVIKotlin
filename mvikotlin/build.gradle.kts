setupMultiplatform()
setupPublication()

kotlinCompat {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":utils-internal"))
                implementation(project(":rx"))
                implementation(project(":rx-internal"))
                implementation(project(":keepers"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }
    }
}
