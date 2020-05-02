buildTargets = setOf(BuildTarget.Android)

setupMultiplatform()
setupPublication()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":rx"))
                implementation(project(":mvikotlin"))
                implementation(project(":mvikotlin-timetravel"))
                implementation(project(":mvikotlin-timetravel-proto-internal"))
                implementation(project(":utils-internal"))
            }
        }
    }
}
