setupMultiplatform()
setupXcodeSync()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }
    }
}
