import com.arkivanov.gradle.kotlin
import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupMultiplatformPublications

setupMultiplatform()
setupMultiplatformPublications()

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":rx"))
                implementation(project(":rx-internal"))
            }
        }

        named("commonTest") {
            dependencies {
                implementation(project(":mvikotlin-test-internal"))
            }
        }
    }
}
