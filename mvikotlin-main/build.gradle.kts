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
            }
        }

        named("commonTest") {
            dependencies {
                implementation(project(":mvikotlin-test-internal"))
            }
        }
    }
}
