plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    targets()
    publications()
    binaryCompatibilityValidator()
}

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
