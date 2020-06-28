package com.arkivanov.mvikotlin.timetravel.export

interface TimeTravelExportSerializer {

    fun serialize(export: TimeTravelExport): Result<ByteArray>

    fun deserialize(data: ByteArray): Result<TimeTravelExport>

    sealed class Result<out T> {
        class Success<out T>(val data: T) : Result<T>()
        class Error(val exception: Exception) : Result<Nothing>()
    }
}
