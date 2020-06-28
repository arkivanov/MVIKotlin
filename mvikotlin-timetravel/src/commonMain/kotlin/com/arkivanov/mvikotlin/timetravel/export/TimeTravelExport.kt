package com.arkivanov.mvikotlin.timetravel.export

import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent

/**
 * Exported time travel data
 *
 * @param recordedEvents a list of recorded time travel events
 * @param unusedStoreStates `States` of all [Store]s that don't have any recorded events
 */
data class TimeTravelExport(
    val recordedEvents: List<TimeTravelEvent>,
    val unusedStoreStates: Map<String, Any>
) : JvmSerializable
