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
    implementation(project(":mvikotlin-main"))
    implementation(project(":mvikotlin-logging"))
    implementation(project(":mvikotlin-timetravel"))
    implementation(project(":sample:reaktive:shared"))

    implementation(enforcedPlatform(deps.kotlinWrappers.kotlinWrappersBom))
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-styled")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-mui")
}
