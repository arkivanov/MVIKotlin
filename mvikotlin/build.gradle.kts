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

android {
    namespace = "com.arkivanov.mvikotlin.core"
}

kotlin {
    setupSourceSets {
        val android by bundle()
        val js by bundle()
        val jsNative by bundle()
        val java by bundle()
        val native by bundle()
        val darwin by bundle()

        jsNative dependsOn common
        native dependsOn jsNative
        darwin dependsOn native
        java dependsOn common
        js dependsOn jsNative

        javaSet dependsOn java
        (nativeSet - darwinSet) dependsOn native
        darwinSet dependsOn darwin
        javaSet dependsOn java

        common.main.dependencies {
            api(deps.essenty.lifecycle)
            api(deps.essenty.instanceKeeper)
        }

        common.test.dependencies {
            implementation(deps.reaktive.reaktive)
        }

        android.main.dependencies {
            implementation(deps.androidx.lifecycle.lifecycleCommonJava8)
            implementation(deps.androidx.lifecycle.lifecycleRuntime)
        }

        all {
            languageSettings {
                optIn("com.arkivanov.mvikotlin.core.utils.internal.InternalMviKotlinApi")
            }
        }
    }
}
