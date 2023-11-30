dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            from(files("deps.versions.toml"))
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://jitpack.io")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.toString() == "com.arkivanov.gradle.setup") {
                useModule("com.github.arkivanov:gradle-setup-plugin:4a116614b3")
            }
        }
    }

    plugins {
        id("com.arkivanov.gradle.setup")
    }
}

if (!startParameter.projectProperties.containsKey("check_publication")) {
    include(":mvikotlin")
    include(":mvikotlin-test-internal")
    include(":mvikotlin-main")
    include(":mvikotlin-logging")
    include(":mvikotlin-timetravel")
    include(":mvikotlin-timetravel-proto-internal")
    include(":mvikotlin-timetravel-client:client-internal")
    include(":mvikotlin-timetravel-client:plugin-idea")
    include(":mvikotlin-timetravel-client:app-desktop")
    include(":mvikotlin-timetravel-client:extension-chrome")
    include(":mvikotlin-extensions-reaktive")
    include(":mvikotlin-extensions-coroutines")
    include(":sample:database")
    include(":sample:reaktive:shared")
    include(":sample:reaktive:app-android")
    include(":sample:reaktive:app-js")
    include(":sample:coroutines:shared")
    include(":sample:coroutines:app-android")
    include(":sample:coroutines:app-js")
} else {
    include(":tools:check-publication")
}
