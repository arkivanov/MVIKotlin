import com.arkivanov.gradle.Target

plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    targets(
        Target.Android,
        Target.Js(mode = Target.Js.Mode.IR),
        Target.Ios(
            arm64 = false, // Comment this line to enable arm64 target, check dependencies as well
        ),
    )
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":utils-internal"))
                implementation(project(":mvikotlin"))
            }
        }
    }
}
