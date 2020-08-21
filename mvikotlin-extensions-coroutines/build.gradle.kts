setupMultiplatform()
setupPublication()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":rx"))
                implementation(project(":utils-internal"))
                implementation(Deps.Badoo.Reaktive.Utils)
                implementation(Deps.Jetbrains.Kotlinx.Coroutines.Core)
            }
        }
    }
}
