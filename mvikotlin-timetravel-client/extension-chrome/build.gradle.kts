import com.arkivanov.gradle.bundle
import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupSourceSets

plugins {
    id("kotlin-multiplatform") // Compose plugin works only with multiplatform https://github.com/JetBrains/compose-jb/issues/658
    id("org.jetbrains.compose")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    js {
        browser()
        binaries.library()
    }
}

kotlin {
    setupSourceSets {
        val js by bundle()

        js.main.dependencies {
            implementation(project(":mvikotlin"))
            implementation(project(":mvikotlin-main"))
            implementation(project(":mvikotlin-timetravel-proto-internal"))
            implementation(project(":mvikotlin-timetravel-client:client-internal"))
            implementation(compose.runtime)
            implementation(compose.web.core)
            implementation(deps.reaktive.reaktive)
        }
    }
}
