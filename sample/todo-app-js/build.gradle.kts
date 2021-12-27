plugins {
    id("org.jetbrains.kotlin.js")
    id("com.arkivanov.gradle.setup")
}

setupJsApp {
    jsApp()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(project(":rx"))
    implementation(project(":mvikotlin"))
    implementation(project(":mvikotlin-main"))
    implementation(project(":mvikotlin-logging"))
    implementation(project(":mvikotlin-timetravel"))
    implementation(project(":sample:todo-reaktive"))
    implementation(project(":sample:todo-coroutines"))
    implementation(deps.kotlinx.kotlinxCoroutinesCore)
    implementation(deps.essenty.instanceKeeper)

    implementation(enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:0.0.1-pre.236-kotlin-1.5.30"))
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-styled")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions")
    implementation("com.ccfraser.muirwik:muirwik-components:0.9.0")

    implementation(npm("core-js", "2.6.5"))
    implementation(npm("svg-inline-loader", "0.8.0"))
    implementation(npm("react", "16.13.0"))
    implementation(npm("react-dom", "16.13.0"))
    implementation(npm("react-is", "16.13.0"))
    implementation(npm("inline-style-prefixer", "5.1.0"))
    implementation(npm("styled-components", "4.3.2"))
    implementation(npm("@material-ui/core", "4.9.14"))
    implementation(npm("@material-ui/icons", "4.9.1"))
}
