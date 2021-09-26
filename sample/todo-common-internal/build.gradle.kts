import com.arkivanov.gradle.Target

plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setup {
    multiplatform(Target.Android, Target.Js(mode = Target.Js.Mode.IR), Target.Ios)
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":mvikotlin"))
                api(project(":sample:todo-common"))
            }
        }
    }
}
