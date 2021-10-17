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
                api(project(":mvikotlin"))
                implementation(project(":mvikotlin-extensions-reaktive"))
                api(project(":sample:todo-common"))
                implementation(project(":sample:todo-common-internal"))
                implementation(deps.reaktive.reaktive)
                api(deps.essenty.instanceKeeper)
            }
        }

        named("commonTest") {
            dependencies {
                implementation(project(":utils-internal"))
                implementation(project(":mvikotlin-main"))
                implementation(deps.reaktive.reaktiveTesting)
            }
        }
    }
}
