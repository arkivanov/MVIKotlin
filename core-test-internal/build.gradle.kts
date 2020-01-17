project.setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                implementation(project(":core-internal"))
                implementation(project(":utils-internal"))
                implementation(Deps.Jetbrains.Kotlin.Test.Common)
                implementation(Deps.Jetbrains.Kotlin.TestAnnotations.Common)
                implementation(Deps.Badoo.Reaktive.Utils)
            }
        }

        jvmCommonMain {
            dependencies {
                implementation(Deps.Jetbrains.Kotlin.Test.Junit)
            }
        }
    }
}
