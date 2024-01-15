package com.arkivanov.mvikotlin.timetravel

import com.arkivanov.mvikotlin.timetravel.proto.internal.convertToByteArray
import com.arkivanov.mvikotlin.timetravel.proto.internal.convertToString

class ContentMessage(
    val senderId: String,
    val receiverId: String,
    val type: String,
    val payload: ByteArray?,
) {
    fun encode(): String =
        "$PREFIX:$senderId:$receiverId:$type:${payload?.convertToString()}"

    fun requirePayload(): ByteArray =
        requireNotNull(payload)

    companion object {
        private const val PREFIX: String = "MVIKotlinTimeTravel"

        fun decode(str: String): ContentMessage? {
            if (!str.startsWith(PREFIX)) {
                return null
            }

            val parts = str.split(":")

            return ContentMessage(
                senderId = parts[1],
                receiverId = parts[2],
                type = parts[3],
                payload = parts.getOrNull(4)?.takeUnless { it == "null" }?.convertToByteArray(),
            )
        }
    }
}
