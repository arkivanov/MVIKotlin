import com.arkivanov.gradle.setupIdeaPlugin

plugins {
    id("org.jetbrains.intellij")
    kotlin("jvm")
    id("com.arkivanov.gradle.setup")
}

setupIdeaPlugin(
    group = "org.arkivanov.mvikotlin.plugin.idea.timetravel",
    version = deps.versions.timeTravelPlugin.get(),
    sinceBuild = "222",
    intellijVersion = "2022.2",
)

dependencies {
    implementation(project(":mvikotlin-timetravel-client:client-internal"))
    implementation(project(":mvikotlin-timetravel-proto-internal"))
    implementation(project(":mvikotlin"))
    implementation(project(":mvikotlin-main"))
    implementation(deps.reaktive.reaktive)
    implementation(deps.russhwolf.multiplatformSettings)
}
