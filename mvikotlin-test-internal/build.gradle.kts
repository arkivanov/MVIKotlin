plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    targets()
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":rx"))
                implementation(project(":utils-internal"))
                implementation(deps.kotlin.kotlinTestCommon)
                implementation(deps.kotlin.kotlinTestAnnotationsCommon)
            }
        }

        named("jsMain") {
            dependencies {
                implementation(deps.kotlin.kotlinTestJs)
            }
        }

        named("javaMain") {
            dependencies {
                implementation(deps.kotlin.kotlinTestJunit)
            }
        }
    }
}
