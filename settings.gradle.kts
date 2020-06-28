enableFeaturePreview("GRADLE_METADATA")

include(":utils-internal")
include(":rx")
include(":rx-internal")
include(":mvikotlin")
include(":mvikotlin-test-internal")
include(":mvikotlin-main")
include(":mvikotlin-logging")
include(":mvikotlin-timetravel")
include(":mvikotlin-timetravel-proto-internal")
include(":mvikotlin-timetravel-client:client-internal")
doIfBuildTargetAvailable<BuildTarget.Jvm> {
    include(":mvikotlin-timetravel-client:plugin-idea")
}
include(":mvikotlin-extensions-reaktive")
include(":mvikotlin-extensions-coroutines")
include(":androidx-lifecycle-interop")
include(":sample:todo-common")
include(":sample:todo-common-internal")
include(":sample:todo-reaktive")
include(":sample:todo-coroutines")
include(":sample:todo-darwin-umbrella")
doIfBuildTargetAvailable<BuildTarget.Android> {
    include(":sample:todo-app-android")
}
doIfBuildTargetAvailable<BuildTarget.Js> {
    include(":sample:todo-app-js")
}

//include(":tools:check-publication")
