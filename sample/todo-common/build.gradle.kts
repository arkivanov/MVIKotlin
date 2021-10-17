import com.arkivanov.gradle.Target

plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    targets(Target.Android, Target.Js(mode = Target.Js.Mode.IR), Target.Ios())
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":utils-internal"))
                implementation(project(":mvikotlin"))
                implementation(deps.essenty.instanceKeeper)
            }
        }
    }
}
