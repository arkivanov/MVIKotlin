package com.arkivanov.mvikotlin.core.store

import kotlinx.serialization.KSerializer

class StoreSerializers<Intent : Any, Action : Any, Message : Any, State : Any, Label : Any>(
    val intentSerializer: KSerializer<Intent>,
    val actionSerializer: KSerializer<Action>,
    val messageSerializer: KSerializer<Message>,
    val stateSerializer: KSerializer<State>,
    val labelSerializer: KSerializer<Label>,
)
