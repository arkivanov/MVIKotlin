project.setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }
    }
}
