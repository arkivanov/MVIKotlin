enableFeaturePreview("GRADLE_METADATA")

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
doIfJvmTargetAvailable {
    include(":mvikotlin-timetravel-client:plugin-idea")
}
include(":mvikotlin-extensions-reaktive")
include(":mvikotlin-extensions-coroutines")
include(":mvikotlin-extensions-androidx")
include(":sample:todo-common")
include(":sample:todo-common-internal")
include(":sample:todo-reaktive")
include(":sample:todo-coroutines")
include(":sample:todo-darwin-umbrella")
doIfAndroidTargetAvailable {
    include(":sample:todo-app-android")
}
doIfJsTargetAvailable {
    include(":sample:todo-app-js")
}
if (find("check_publication")?.toString()?.toBoolean() == true) {
    include(":tools:check-publication")
}

enum class BuildType {
    ALL, METADATA, NON_NATIVE, ANDROID, JVM, JS, LINUX, IOS, MAC_OS
}

val ExtensionAware.buildType: BuildType
    get() =
        find("build_type")
            ?.toString()
            ?.let(BuildType::valueOf)
            ?: BuildType.ALL

fun ExtensionAware.find(key: String) =
    if (extra.has(key)) extra.get(key) else null

fun doIfJvmTargetAvailable(block: () -> Unit) {
    if (buildType in setOf(BuildType.ALL, BuildType.METADATA, BuildType.NON_NATIVE, BuildType.JVM)) {
        block()
    }
}

fun doIfAndroidTargetAvailable(block: () -> Unit) {
    if (buildType in setOf(BuildType.ALL, BuildType.METADATA, BuildType.NON_NATIVE, BuildType.ANDROID)) {
        block()
    }
}

fun doIfJsTargetAvailable(block: () -> Unit) {
    if (buildType in setOf(BuildType.ALL, BuildType.METADATA, BuildType.NON_NATIVE, BuildType.JS)) {
        block()
    }
}
