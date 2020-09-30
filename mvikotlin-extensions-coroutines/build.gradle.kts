setupMultiplatform()
setupPublication()

kotlinCompat {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":rx"))
                implementation(project(":utils-internal"))
                implementation(Deps.Jetbrains.Kotlinx.Coroutines.Core)
            }
        }
    }
}
