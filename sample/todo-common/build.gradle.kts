import com.arkivanov.gradle.Target

plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setup {
    multiplatform(Target.Android, Target.Js(mode = Target.Js.Mode.IR), Target.Ios(isAppleSiliconEnabled = false))
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
