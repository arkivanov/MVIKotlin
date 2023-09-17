package com.arkivanov.mvikotlin.timetravel.proto.internal.thread

import kotlin.native.concurrent.ObsoleteWorkersApi
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze

@OptIn(ObsoleteWorkersApi::class)
internal fun Worker.execute(block: () -> Unit) {
    execute(TransferMode.SAFE, { block.freeze() }, { it.invoke() })
}
