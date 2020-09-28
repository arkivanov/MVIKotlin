buildTargets = setOf(BuildTarget.Android, BuildTarget.Js, BuildTarget.IosX64, BuildTarget.IosArm64)

setupMultiplatform()
setupXcodeSync()

kotlinCompat {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":keepers"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }
    }
}
