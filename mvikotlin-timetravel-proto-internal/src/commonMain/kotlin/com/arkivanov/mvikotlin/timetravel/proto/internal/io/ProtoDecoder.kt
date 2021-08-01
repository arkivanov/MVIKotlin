package com.arkivanov.mvikotlin.timetravel.proto.internal.io

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.ProtoObject
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventvalue.readTimeTravelEventValue
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelcomand.readTimeTravelCommand
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelexport.readTimeTravelExport
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.readTimeTravelStateUpdate

class ProtoDecoder {

    fun decode(data: ByteArray): ProtoObject {
        val reader = DataReader(data)

        val protoVersion = reader.readInt()
        check(protoVersion == PROTO_VERSION) {
            "Invalid proto version received. Expected $PROTO_VERSION, received $protoVersion."
        }

        return when (reader.readEnum<ProtoObjectType>()) {
            ProtoObjectType.TIME_TRAVEL_STATE_UPDATE -> reader.readTimeTravelStateUpdate()
            ProtoObjectType.TIME_TRAVEL_COMMAND -> reader.readTimeTravelCommand()
            ProtoObjectType.TIME_TRAVEL_EVENT_VALUE -> reader.readTimeTravelEventValue()
            ProtoObjectType.TIME_TRAVEL_EXPORT -> reader.readTimeTravelExport()
        }
    }
}
