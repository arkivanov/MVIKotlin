import com.arkivanov.gradle.Target

plugins {
    id("kotlin-multiplatform") // Compose plugin works only with multiplatform https://github.com/JetBrains/compose-jb/issues/658
    id("org.jetbrains.compose")
    id("com.arkivanov.gradle.setup")
}

setupMultiplatform {
    targets(
        Target.Js(
            mode = Target.Js.Mode.IR,
            environments = setOf(Target.Js.Environment.BROWSER),
            binary = Target.Js.Binary.LIBRARY,
        )
    )
}

kotlin {
    js(IR) {
        browser()
        binaries.library()
    }

    sourceSets {
        named("jsMain") {
            dependencies {
                implementation(project(":mvikotlin-main"))
                implementation(project(":mvikotlin-timetravel-proto-internal"))
                implementation(project(":mvikotlin-timetravel-client:client-internal"))
                implementation(compose.runtime)
                implementation(compose.web.core)
                implementation(deps.reaktive.reaktive)
            }
        }
    }
}
