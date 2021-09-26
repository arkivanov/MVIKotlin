import com.arkivanov.gradle.Target

plugins {
    id("kotlin-multiplatform")
    id("com.arkivanov.gradle.setup")
}

setup {
    multiplatform(Target.Jvm)
}

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
