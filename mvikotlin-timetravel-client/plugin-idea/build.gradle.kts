plugins {
    id("org.jetbrains.intellij")
    kotlin("jvm")
    id("com.arkivanov.gradle.setup")
}

setupIdeaPlugin {
    ideaPlugin(
        group = "org.arkivanov.mvikotlin.plugin.idea.timetravel",
        version = deps.versions.mvikotlin.get(),
        sinceBuild = "193",
        intellijVersion = "2019.3",
    )
}

dependencies {
    implementation(project(":mvikotlin-timetravel-client:client-internal"))
    implementation(project(":mvikotlin-timetravel-proto-internal"))
    implementation(project(":mvikotlin"))
    implementation(project(":mvikotlin-main"))
    implementation(deps.reaktive.reaktive)
    implementation(deps.russhwolf.multiplatformSettings)
}
