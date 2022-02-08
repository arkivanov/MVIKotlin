package com.arkivanov.mvikotlin.timetravel

internal external interface ContentMessage {
    var senderId: String
    var receiverId: String
    var type: String
    var payload: Any
}
