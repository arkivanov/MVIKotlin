setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                api(project(":sample:todo-common"))
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }
    }
}
