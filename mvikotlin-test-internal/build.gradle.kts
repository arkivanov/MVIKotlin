setupMultiplatform()

kotlinCompat {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":rx"))
                implementation(project(":utils-internal"))
                implementation(Deps.Jetbrains.Kotlin.Test)
                implementation(Deps.Jetbrains.Kotlin.TestAnnotations.Common)
            }
        }

        jvmCommonMain {
            dependencies {
                implementation(Deps.Jetbrains.Kotlin.Test.Junit)
            }
        }
    }
}
