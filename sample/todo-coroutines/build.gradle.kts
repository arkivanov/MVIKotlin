import com.arkivanov.gradle.Target

plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    targets(Target.Android, Target.Js(mode = Target.Js.Mode.IR))
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                api(project(":mvikotlin"))
                implementation(project(":mvikotlin-extensions-coroutines"))
                api(project(":sample:todo-common"))
                implementation(project(":sample:todo-common-internal"))
                implementation(deps.kotlinx.kotlinxCoroutinesCore)
                api(deps.essenty.instanceKeeper)
            }
        }

        named("commonTest") {
            dependencies {
                implementation(project(":utils-internal"))
                implementation(project(":mvikotlin-main"))
                implementation(project(":rx"))
            }
        }
    }
}
