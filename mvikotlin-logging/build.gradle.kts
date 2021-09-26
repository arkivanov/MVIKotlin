plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setup {
    multiplatform()
    multiplatformPublications()
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":mvikotlin"))
                implementation(project(":utils-internal"))
            }
        }

        named("commonTest") {
            dependencies {
                implementation(project(":mvikotlin-test-internal"))
                implementation(project(":rx"))
            }
        }
    }
}
