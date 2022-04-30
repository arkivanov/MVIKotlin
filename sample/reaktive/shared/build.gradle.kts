import com.arkivanov.gradle.Target
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

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
    targets
        .filterIsInstance<KotlinNativeTarget>()
        .filter { it.konanTarget.family == Family.IOS }
        .forEach { target ->
            target.binaries.framework {
                baseName = "Todo"

                export(project(":sample:database"))
                export(deps.reaktive.reaktive)
                export(deps.essenty.lifecycle)
                export(deps.essenty.instanceKeeper)
                export(project(":mvikotlin"))
                export(project(":mvikotlin-main"))
                export(project(":mvikotlin-logging"))
                export(project(":mvikotlin-timetravel"))
            }
        }

    sourceSets {
        named("commonMain") {
            dependencies {
                api(project(":mvikotlin"))
                implementation(project(":mvikotlin-extensions-reaktive"))
                api(project(":sample:database"))
                api(deps.reaktive.reaktive)
                api(deps.essenty.lifecycle)
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

        named("darwinMain") {
            dependencies {
                api(project(":mvikotlin-main"))
                api(project(":mvikotlin-logging"))
                api(project(":mvikotlin-timetravel"))
            }
        }
    }
}
