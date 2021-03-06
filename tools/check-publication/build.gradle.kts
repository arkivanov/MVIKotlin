setupMultiplatform()

val version = property("mvikotlin.version").toString()

repositories {
    maven {
        setUrl("https://oss.sonatype.org/content/groups/staging/")

        credentials  {
            setUsername("arkivanov")
            setPassword(System.getenv("SONATYPE_PASSWORD"))
        }
    }
}

kotlinCompat {
    sourceSets {
        commonMain {
            dependencies {
                implementation("com.arkivanov.mvikotlin:rx:$version")
                implementation("com.arkivanov.mvikotlin:keepers:$version")
                implementation("com.arkivanov.mvikotlin:mvikotlin:$version")
                implementation("com.arkivanov.mvikotlin:mvikotlin-main:$version")
                implementation("com.arkivanov.mvikotlin:mvikotlin-logging:$version")
                implementation("com.arkivanov.mvikotlin:mvikotlin-timetravel:$version")
                implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-reaktive:$version")
                implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:$version")
            }
        }

        androidMain {
            dependencies {
                implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-androidx:$version")
            }
        }
    }
}
