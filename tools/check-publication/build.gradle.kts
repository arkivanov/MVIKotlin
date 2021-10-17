import com.arkivanov.gradle.kotlin
import com.arkivanov.gradle.setupMultiplatform
import com.arkivanov.gradle.setupMultiplatformPublications

setupMultiplatform {
    targets()
}

repositories {
    maven("https://oss.sonatype.org/content/groups/staging/") {
        credentials {
            username = "arkivanov"
            password = System.getenv("SONATYPE_PASSWORD")
        }
    }
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                val version = deps.versions.mvikotlin.get()
                implementation("com.arkivanov.mvikotlin:rx:$version")
                implementation("com.arkivanov.mvikotlin:mvikotlin:$version")
                implementation("com.arkivanov.mvikotlin:mvikotlin-main:$version")
                implementation("com.arkivanov.mvikotlin:mvikotlin-logging:$version")
                implementation("com.arkivanov.mvikotlin:mvikotlin-timetravel:$version")
                implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-reaktive:$version")
                implementation("com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines:$version")
            }
        }
    }
}
