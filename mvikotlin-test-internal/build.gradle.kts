import com.arkivanov.gradle.kotlin
import com.arkivanov.gradle.setupMultiplatform

setupMultiplatform()

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":rx"))
                implementation(project(":utils-internal"))
                implementation(deps.kotlin.kotlinTestCommon)
                implementation(deps.kotlin.kotlinTestAnnotationsCommon)
            }
        }

        named("jsMain") {
            dependencies {
                implementation(deps.kotlin.kotlinTestJs)
            }
        }

        named("javaMain") {
            dependencies {
                implementation(deps.kotlin.kotlinTestJunit)
            }
        }
    }
}
