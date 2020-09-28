plugins {
    kotlin("js")
}

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-js-wrappers")

}

val kotlin_version = "pre.94-kotlin-1.3.70" // for kotlin-wrappers
val kotlin_react_version = "16.13.0-$kotlin_version"
val kotlin_styled_version = "1.0.0-$kotlin_version"
val muirwik_version = "0.4.1"

dependencies {
    implementation(Deps.Jetbrains.Kotlin.StdLib.Js)
    implementation(Deps.Jetbrains.Kotlinx.Coroutines.Core)

    implementation("org.jetbrains:kotlin-react:$kotlin_react_version")
    implementation("org.jetbrains:kotlin-react-dom:$kotlin_react_version")
    implementation("org.jetbrains:kotlin-styled:$kotlin_styled_version")

    implementation("org.jetbrains:kotlin-extensions:1.0.1-$kotlin_version")

    implementation("com.ccfraser.muirwik:muirwik-components:$muirwik_version")

    implementation(project(":rx"))
    implementation(project(":keepers"))
    implementation(project(":mvikotlin"))
    implementation(project(":mvikotlin-main"))
    implementation(project(":mvikotlin-logging"))
    implementation(project(":mvikotlin-timetravel"))
    implementation(project(":sample:todo-reaktive"))
    implementation(project(":sample:todo-coroutines"))

    implementation(npm("core-js", "2.6.5"))
    implementation(npm("svg-inline-loader", "0.8.0"))
    implementation(npm("abort-controller"))
    implementation(npm("react", "16.13.0"))
    implementation(npm("react-dom", "16.13.0"))
    implementation(npm("react-is", "16.13.0"))
    implementation(npm("inline-style-prefixer", "5.1.0"))
    implementation(npm("styled-components", "4.3.2"))
    implementation(npm("@material-ui/core", "4.9.14"))
    implementation(npm("@material-ui/icons", "4.9.1"))
}

kotlin.withGroovyBuilder {
    "js" {
        "useCommonJs"()
        "browser"()
        "binaries" {
            "executable"()
        }
    }
}
