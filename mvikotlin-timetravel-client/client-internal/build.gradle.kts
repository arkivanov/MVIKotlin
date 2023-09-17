import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupSourceSets

plugins {
    id("kotlin-multiplatform")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    jvm()
    js { browser() }
}

kotlin {
    setupSourceSets {
        common.main.dependencies {
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
