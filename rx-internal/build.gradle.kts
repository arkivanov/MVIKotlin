plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    targets()
    publications()
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":rx"))
                implementation(project(":utils-internal"))
            }
        }
    }
}
