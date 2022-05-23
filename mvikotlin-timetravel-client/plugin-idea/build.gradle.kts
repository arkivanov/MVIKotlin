import com.arkivanov.gradle.setupIdeaPlugin

plugins {
    id("org.jetbrains.intellij")
    kotlin("jvm")
    id("com.arkivanov.gradle.setup")
}

setupIdeaPlugin(
    group = "org.arkivanov.mvikotlin.plugin.idea.timetravel",
    version = deps.versions.mvikotlin.get(),
    sinceBuild = "211",
    intellijVersion = "2021.1",
)

dependencies {
    implementation(project(":mvikotlin-timetravel-client:client-internal"))
    implementation(project(":mvikotlin-timetravel-proto-internal"))
    implementation(project(":mvikotlin"))
    implementation(project(":mvikotlin-main"))
    implementation(deps.reaktive.reaktive)
    implementation(deps.russhwolf.multiplatformSettings)
}
