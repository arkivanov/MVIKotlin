buildTargets = setOf(BuildTarget.Jvm, BuildTarget.Android)

setupMultiplatform()
setupPublication()


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":utils-internal"))
            }
        }
    }
}
