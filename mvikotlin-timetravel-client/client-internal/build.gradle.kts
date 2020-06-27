import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

buildTargets = setOf(BuildTarget.Jvm, BuildTarget.MacOsX64)

setupMultiplatform()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":mvikotlin-timetravel-proto-internal"))
                api(project(":mvikotlin"))
                implementation(project(":mvikotlin-main"))
                implementation(project(":mvikotlin-extensions-reaktive"))
                implementation(Deps.Badoo.Reaktive.Reaktive)
                implementation(Deps.Badoo.Reaktive.ReaktiveAnnotations)
            }
        }
    }

    doIfBuildTargetAvailable<BuildTarget.MacOsX64> {
        macosX64Compat().setupBinaries()
    }
}

fun KotlinNativeTarget.setupBinaries() {
    binaries {
        framework {
            baseName = "TimeTravelClient"
            freeCompilerArgs = freeCompilerArgs.plus("-Xobjc-generics").toMutableList()

            export(project(":mvikotlin-timetravel-proto-internal"))
            export(project(":mvikotlin"))
        }
    }
}

doIfBuildTargetAvailable<BuildTarget.MacOsX64> {
    val packMacForXcode by tasks.creating(Sync::class) {
        val targetDir = File(buildDir, "xcode-mac-frameworks")

        kotlin {
            /// selecting the right configuration for the iOS
            /// framework depending on the environment
            /// variables set by Xcode build
            val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
            val framework = targets
                .getByName<KotlinNativeTarget>("macosX64")
                .binaries.getFramework(mode)
            inputs.property("mode", mode)
            dependsOn(framework.linkTask)

            from({ framework.outputDirectory })
            into(targetDir)
        }
        /// generate a helpful ./gradlew wrapper with embedded Java path
        doLast {
            val gradlew = File(targetDir, "gradlew")
            gradlew.writeText(
                "#!/bin/bash\n"
                        + "export 'JAVA_HOME=${System.getProperty("java.home")}'\n"
                        + "cd '${rootProject.rootDir}'\n"
                        + "./gradlew \$@\n"
            )
            gradlew.setExecutable(true)
        }
    }
}