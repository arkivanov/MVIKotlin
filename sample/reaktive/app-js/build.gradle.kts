import com.arkivanov.gradle.bundle
import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupSourceSets

plugins {
    id("kotlin-multiplatform")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    js {
        browser()
        binaries.executable()
    }
}

kotlin {
    setupSourceSets {
        val js by bundle()

        js.main.dependencies {
            implementation(project(":rx"))
            implementation(project(":mvikotlin-main"))
            implementation(project(":mvikotlin-logging"))
            implementation(project(":mvikotlin-timetravel"))
            implementation(project(":sample:reaktive:shared"))

            implementation(project.dependencies.platform(deps.kotlinWrappers.kotlinWrappersBom))
            implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
            implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
            implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom")
            implementation("org.jetbrains.kotlin-wrappers:kotlin-styled")
            implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions")
            implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion")
            implementation("org.jetbrains.kotlin-wrappers:kotlin-mui")
        }
    }
}
