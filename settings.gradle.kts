enableFeaturePreview("GRADLE_METADATA")

include(":utils-internal")
include(":core")
include(":core-internal")
include(":core-test-internal")
include(":mvikotlin")
include(":timetravel")
include(":logging")
include(":extensions-reaktive")
include(":extensions-coroutines")
include(":sample:todo-common")
include(":sample:todo-common-internal")
include(":sample:todo-reaktive")
include(":sample:todo-coroutines")

//include(":tools:check-publication")

doIfBuildTargetAvailable<BuildTarget.Darwin> {
    include(":sample:todo-darwin-umbrella")
}

doIfBuildTargetAvailable<BuildTarget.Android> {
    include(":sample:todo-app-android")
}
