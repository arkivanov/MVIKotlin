import com.arkivanov.gradle.bundle
import com.arkivanov.gradle.dependsOn
import com.arkivanov.gradle.iosCompat
import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupSourceSets
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
    id("kotlin-multiplatform")
    id("com.android.library")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    android()
    js(IR) { browser() }
    iosCompat(
        arm64 = null, // Comment out to enable arm64 target
    )
}

android {
    namespace = "com.arkivanov.mvikotlin.sample.coroutines.shared"
}

kotlin {
    targets
        .filterIsInstance<KotlinNativeTarget>()
        .filter { it.konanTarget.family == Family.IOS }
        .forEach { target ->
            target.binaries.framework {
                baseName = "Todo"

                export(project(":sample:database"))
                export(deps.essenty.lifecycle)
                export(deps.essenty.instanceKeeper)
                export(project(":mvikotlin"))
                export(project(":mvikotlin-main"))
                export(project(":mvikotlin-logging"))
                export(project(":mvikotlin-timetravel"))
            }
        }

    setupSourceSets {
        val darwin by bundle()

        darwin dependsOn common
        darwinSet dependsOn darwin

        common.main.dependencies {
            api(project(":mvikotlin"))
            implementation(project(":mvikotlin-extensions-coroutines"))
            api(project(":sample:database"))
            api(deps.kotlinx.kotlinxCoroutinesCore)
            api(deps.essenty.lifecycle)
            api(deps.essenty.instanceKeeper)
        }

        common.test.dependencies {
            implementation(project(":utils-internal"))
            implementation(project(":mvikotlin-main"))
        }

        darwin.main.dependencies {
            api(project(":mvikotlin-main"))
            api(project(":mvikotlin-logging"))
            api(project(":mvikotlin-timetravel"))
        }
    }
}

kotlin.targets.withType<KotlinNativeTarget> {
    binaries.all {
        binaryOptions["memoryModel"] = "experimental"
    }
}
