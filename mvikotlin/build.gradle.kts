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
