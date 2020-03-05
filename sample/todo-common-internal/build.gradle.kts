setupMultiplatform()

kotlin {
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
