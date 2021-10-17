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
                implementation(project(":mvikotlin"))
                implementation(project(":rx"))
                implementation(project(":rx-internal"))
                implementation(project(":utils-internal"))
                implementation(project(":mvikotlin-timetravel-proto-internal"))
            }
        }

        named("commonTest") {
            dependencies {
                implementation(project(":mvikotlin-test-internal"))
            }
        }

        named("androidMain") {
            dependencies {
                implementation(deps.androidx.core.coreKtx)
                implementation(deps.androidx.appcompat.appcompat)
                implementation(deps.androidx.recyclerview.recyclerview)
                implementation(deps.androidx.constraintlayout.constraintlayout)
            }
        }
    }
}
