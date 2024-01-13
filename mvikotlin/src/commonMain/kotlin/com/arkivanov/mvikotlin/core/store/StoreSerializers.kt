package com.arkivanov.mvikotlin.core.store

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.serializer

class StoreSerializers<Intent : Any, Action : Any, Message : Any, State : Any, Label : Any>(
    val intentSerializer: KSerializer<Intent>? = null,
    val actionSerializer: KSerializer<Action>? = null,
    val messageSerializer: KSerializer<Message>? = null,
    val stateSerializer: KSerializer<State>? = null,
    val labelSerializer: KSerializer<Label>? = null,
)

//inline fun <reified Intent : Any, reified Action : Any, reified Message : Any, reified State : Any, reified Label : Any> storeSerializers(
//): StoreSerializers<Intent, Action, Message, State, Label> =
//    StoreSerializers(
//        intentSerializer = serializerOrNull(),
//        actionSerializer = serializerOrNull(),
//        messageSerializer = serializerOrNull(),
//        stateSerializer = serializerOrNull(),
//        labelSerializer = serializerOrNull(),
//    )
//
//@PublishedApi
//internal inline fun <reified T> serializerOrNull(): KSerializer<T>? =
//    try {
//        serializer<T>()
//    } catch (e: SerializationException) {
//        null
//    }
