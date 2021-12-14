@file:JsQualifier("chrome.scripting")

package chrome.scripting

external fun executeScript(
    injection: ScriptInjection,
    callback: (Array<InjectionResult>) -> Unit = definedExternally
)

external interface ScriptInjection {
    var target: InjectionTarget
    var func: dynamic
}

external interface InjectionTarget {
    var tabId: Int
}

external interface InjectionResult {
    val result: Any?
}
