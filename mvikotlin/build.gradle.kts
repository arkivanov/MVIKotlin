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
                implementation(project(":rx"))
                implementation(project(":rx-internal"))
                api(deps.essenty.lifecycle)
                api(deps.essenty.instanceKeeper)
            }
        }

        named("androidMain") {
            dependencies {
                implementation(deps.androidx.lifecycle.lifecycleCommonJava8)
                implementation(deps.androidx.lifecycle.lifecycleRuntime)
            }
        }
    }
}
