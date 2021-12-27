enableFeaturePreview("VERSION_CATALOGS")

@Suppress("UnstableApiUsage")
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
                useModule("com.github.arkivanov:gradle-setup-plugin:aeb858bc86")
            }
        }
    }

    plugins {
        id("com.arkivanov.gradle.setup")
    }
}

if (!startParameter.projectProperties.containsKey("check_publication")) {
    include(":utils-internal")
    include(":rx")
    include(":rx-internal")
    include(":keepers")
    include(":mvikotlin")
    include(":mvikotlin-test-internal")
    include(":mvikotlin-main")
    include(":mvikotlin-logging")
    include(":mvikotlin-timetravel")
    include(":mvikotlin-timetravel-proto-internal")
    include(":mvikotlin-timetravel-client:client-internal")
    include(":mvikotlin-timetravel-client:plugin-idea")
    include(":mvikotlin-timetravel-client:app-desktop")
    include(":mvikotlin-extensions-reaktive")
    include(":mvikotlin-extensions-coroutines")
    include(":sample:todo-common")
    include(":sample:todo-common-internal")
    include(":sample:todo-reaktive")
    include(":sample:todo-coroutines")
    include(":sample:todo-darwin-umbrella")
    include(":sample:todo-app-android")
    include(":sample:todo-app-js")
} else {
    include(":tools:check-publication")
}
