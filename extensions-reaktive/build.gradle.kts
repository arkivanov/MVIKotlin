project.setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":core"))
                implementation(project(":core-internal"))
                implementation(project(":utils-internal"))
                implementation(Deps.Badoo.Reaktive.Reaktive)
            }
        }
    }
}
