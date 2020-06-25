buildTargets = setOf(BuildTarget.Jvm)

setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":mvikotlin-timetravel-proto-internal"))
                api(project(":mvikotlin"))
                implementation(project(":mvikotlin-main"))
                implementation(project(":mvikotlin-extensions-reaktive"))
                implementation(Deps.Badoo.Reaktive.Reaktive)
                implementation(Deps.Badoo.Reaktive.ReaktiveAnnotations)
            }
        }
    }
}
