import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

setupMultiplatform()

kotlin {
    iosX64().setupIosBinaries()
    iosArm64().setupIosBinaries()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":mvikotlin-main"))
                api(project(":mvikotlin-logging"))
                api(project(":mvikotlin-timetravel"))
                api(project(":sample:todo-common"))
                api(project(":sample:todo-reaktive"))
            }
        }
    }
}

fun KotlinNativeTarget.setupIosBinaries() {
    binaries {
        framework {
            baseName = "TodoLib"
            freeCompilerArgs = freeCompilerArgs.plus("-Xobjc-generics").toMutableList()

            export(project(":mvikotlin-main"))
            export(project(":mvikotlin-logging"))
            export(project(":mvikotlin-timetravel"))
            export(project(":sample:todo-common"))
            export(project(":sample:todo-reaktive"))
        }
    }
}
