import com.arkivanov.gradle.Target
import com.arkivanov.gradle.kotlin
import com.arkivanov.gradle.setupMultiplatform

setupMultiplatform(Target.Ios)

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

    ios {
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
}
