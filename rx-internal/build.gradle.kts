import com.arkivanov.gradle.bundle
import com.arkivanov.gradle.dependsOn
import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupPublication
import com.arkivanov.gradle.setupSourceSets

plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform()
setupPublication()


android {
    namespace = "com.arkivanov.mvikotlin.rx.internal"
}
kotlin {
    setupSourceSets {
        val native by bundle()
        val darwin by bundle()
        val java by bundle()

        native dependsOn common
        darwin dependsOn native
        java dependsOn common
        javaSet dependsOn java
        linuxSet dependsOn native
        darwinSet dependsOn darwin

        common.main.dependencies {
            implementation(project(":rx"))
            implementation(project(":utils-internal"))
        }

        common.test.dependencies {
            implementation(deps.reaktive.reaktive)
        }
    }
}
