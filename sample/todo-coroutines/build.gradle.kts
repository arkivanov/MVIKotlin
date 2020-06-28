buildTargets = setOf(BuildTarget.Android, BuildTarget.Js)

setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":mvikotlin-extensions-coroutines"))
                api(project(":sample:todo-common"))
                implementation(project(":sample:todo-common-internal"))
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
