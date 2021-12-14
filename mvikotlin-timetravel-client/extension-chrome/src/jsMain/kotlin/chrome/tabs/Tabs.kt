@file:JsQualifier("chrome.tabs")

package chrome.tabs

external fun query(
    queryInfo: QueryInfo,
    callback: (Array<Tab>) -> Unit
)

external interface QueryInfo {
    var active: Boolean
    var currentWindow: Boolean
}

external interface Tab {
    val id: Int
}
