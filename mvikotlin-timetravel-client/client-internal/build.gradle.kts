import com.arkivanov.gradle.Target
import com.arkivanov.gradle.kotlin
import com.arkivanov.gradle.setupMultiplatform

setupMultiplatform(Target.Jvm)

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":rx"))
                implementation(project(":mvikotlin-timetravel-proto-internal"))
                implementation(project(":mvikotlin"))
                implementation(project(":mvikotlin-extensions-reaktive"))
                implementation(deps.reaktive.reaktive)
                implementation(deps.reaktive.reaktiveAnnotations)
                implementation(deps.russhwolf.multiplatformSettings)
            }
        }
    }
}
