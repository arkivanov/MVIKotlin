import com.arkivanov.gradle.Target
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("kotlin-multiplatform")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    targets(Target.Ios())
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                api(project(":mvikotlin-main"))
                api(project(":mvikotlin-logging"))
                api(project(":mvikotlin-timetravel"))
                api(project(":sample:todo-reaktive"))
            }
        }
    }

    fun KotlinNativeTarget.setupBinaries() {
        binaries {
            framework {
                baseName = "TodoLib"
                transitiveExport = true

                export(project(":mvikotlin-main"))
                export(project(":mvikotlin-logging"))
                export(project(":mvikotlin-timetravel"))
                export(project(":sample:todo-reaktive"))
            }
        }
    }

    iosX64 { setupBinaries() }
    iosArm64 { setupBinaries() }
    iosSimulatorArm64 { setupBinaries() }
}
