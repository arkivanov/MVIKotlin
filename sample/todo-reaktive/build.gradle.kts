project.setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                implementation(project(":extensions-reaktive"))
                api(project(":sample:todo-common"))
                implementation(Deps.Badoo.Reaktive.Reaktive)
            }
        }
    }
}
