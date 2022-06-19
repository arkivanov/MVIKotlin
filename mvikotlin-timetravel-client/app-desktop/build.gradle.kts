import com.arkivanov.gradle.bundle
import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupSourceSets
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("kotlin-multiplatform")
    id("com.arkivanov.gradle.setup")
    id("org.jetbrains.compose")
}

setupMultiplatform {
    jvm {
        withJava()
    }
}

kotlin {
    setupSourceSets {
        val jvm by bundle()


        jvm.main.dependencies {
            implementation(project(":mvikotlin"))
            implementation(project(":mvikotlin-main"))
            implementation(project(":mvikotlin-timetravel-client:client-internal"))
            implementation(project(":mvikotlin-timetravel-proto-internal"))
            implementation(deps.reaktive.reaktive)
            implementation(deps.reaktive.coroutinesInterop)
            implementation(deps.russhwolf.multiplatformSettings)
            implementation(compose.desktop.currentOs)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.materialIconsExtended)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.arkivanov.mvikotlin.timetravel.client.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "MVIKotlin Time Travel Client"
            packageVersion = deps.versions.timeTravelApp.get()

            windows {
                upgradeUuid = "B0B34196-90BE-4398-99BE-8E650EBECC78"
            }
        }
    }
}
