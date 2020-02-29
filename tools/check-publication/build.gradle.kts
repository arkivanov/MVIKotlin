// ./gradlew :tools:check-publication -Pversion=2.0.0-preview1 bintray_api_key=

setupMultiplatform()

val version = property("version").toString()

repositories {
    maven {
        setUrl("https://dl.bintray.com/arkivanov/maven/")

        credentials  {
            setUsername("arkivanov")
            setPassword(property("bintray_api_key").toString())
        }
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("com.arkivanov.mvikotlin:core:$version")
                implementation("com.arkivanov.mvikotlin:mvikotlin:$version")
                implementation("com.arkivanov.mvikotlin:logging:$version")
                implementation("com.arkivanov.mvikotlin:timetravel:$version")
                implementation("com.arkivanov.mvikotlin:extensions-reaktive:$version")
                implementation("com.arkivanov.mvikotlin:extensions-coroutines:$version")
            }
        }
    }
}
