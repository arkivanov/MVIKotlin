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
                implementation(Deps.Jetbrains.Kotlinx.Coroutines.Core.Common)
            }
        }

        jvmCommonMain {
            dependencies {
                implementation(Deps.Jetbrains.Kotlinx.Coroutines.Core)
            }
        }

        nativeCommonMain {
            dependencies {
                implementation(Deps.Jetbrains.Kotlinx.Coroutines.Core.Native)
            }
        }

        jsMain {
            dependencies {
                implementation(Deps.Jetbrains.Kotlinx.Coroutines.Core.Js)
            }
        }
    }
}
