import com.arkivanov.gradle.kotlin
import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupMultiplatformPublications

setupMultiplatform()
setupMultiplatformPublications()

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":utils-internal"))
            }
        }

        named("androidMain") {
            dependencies {
                implementation(deps.androidx.lifecycle.lifecycleViewmodel)
                implementation(deps.androidx.lifecycle.lifecycleViewmodelSavedstate)
            }
        }

        named("androidTest") {
            dependencies {
                implementation(deps.androidx.lifecycle.lifecycleRuntime)
            }
        }
    }
}
