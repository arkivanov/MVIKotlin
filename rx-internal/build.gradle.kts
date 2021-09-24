import com.arkivanov.gradle.kotlin
import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupMultiplatformPublications

setupMultiplatform()
setupMultiplatformPublications()

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":rx"))
                implementation(project(":utils-internal"))
            }
        }
    }
}
