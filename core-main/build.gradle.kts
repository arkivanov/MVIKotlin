project.setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                implementation(project(":core-internal"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }
    }
}
