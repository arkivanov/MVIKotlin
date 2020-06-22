import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":rx"))
                api(project(":mvikotlin"))
                api(project(":mvikotlin-main"))
                api(project(":mvikotlin-logging"))
                api(project(":mvikotlin-timetravel"))
                api(project(":sample:todo-common"))
                api(project(":sample:todo-reaktive"))
            }
        }
    }

    iosX64Compat().setupBinaries()
    iosArm64Compat().setupBinaries()
}

fun KotlinNativeTarget.setupBinaries() {
    binaries {
        framework {
            baseName = "TodoLib"
            freeCompilerArgs = freeCompilerArgs.plus("-Xobjc-generics").toMutableList()

            export(project(":rx"))
            export(project(":mvikotlin"))
            export(project(":mvikotlin-main"))
            export(project(":mvikotlin-logging"))
            export(project(":mvikotlin-timetravel"))
            export(project(":sample:todo-common"))
            export(project(":sample:todo-reaktive"))
        }
    }
}
