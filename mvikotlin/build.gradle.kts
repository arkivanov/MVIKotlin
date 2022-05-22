import com.arkivanov.gradle.bundle
import com.arkivanov.gradle.dependsOn
import com.arkivanov.gradle.setupBinaryCompatibilityValidator
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
setupBinaryCompatibilityValidator()

kotlin {
    setupSourceSets {
        val android by bundle()
        val js by bundle()
        val jsNative by bundle()
        val java by bundle()

        jsNative dependsOn common
        java dependsOn common
        js dependsOn jsNative
        javaSet dependsOn java
        nativeSet dependsOn jsNative

        common.main.dependencies {
            implementation(project(":utils-internal"))
            implementation(project(":rx"))
            implementation(project(":rx-internal"))
            api(deps.essenty.lifecycle)
            api(deps.essenty.instanceKeeper)
        }

        android.main.dependencies {
            implementation(deps.androidx.lifecycle.lifecycleCommonJava8)
            implementation(deps.androidx.lifecycle.lifecycleRuntime)
        }
    }
}
