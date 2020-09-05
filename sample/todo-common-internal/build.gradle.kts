buildTargets = setOf(BuildTarget.Android, BuildTarget.Js, BuildTarget.IosX64, BuildTarget.IosArm64)

setupMultiplatform()

kotlinCompat {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                api(project(":sample:todo-common"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }
    }
}
