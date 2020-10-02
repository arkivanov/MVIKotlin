buildTargets = setOf(BuildTarget.Android, BuildTarget.Js, BuildTarget.IosX64, BuildTarget.IosArm64)

setupMultiplatform()
setupXcodeSync()

kotlinCompat {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":keepers"))
                implementation(project(":mvikotlin-extensions-reaktive"))
                api(project(":sample:todo-common"))
                implementation(project(":sample:todo-common-internal"))
                implementation(Deps.Badoo.Reaktive.Reaktive)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":utils-internal"))
                implementation(project(":mvikotlin-main"))
                implementation(Deps.Badoo.Reaktive.ReaktiveTesting)
            }
        }
    }
}
