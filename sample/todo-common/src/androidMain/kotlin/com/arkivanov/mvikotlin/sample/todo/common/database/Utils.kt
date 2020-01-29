package com.arkivanov.mvikotlin.sample.todo.common.database

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

internal fun SQLiteDatabase.queryBy(
    table: String,
    columns: Array<String>? = null,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    groupBy: String? = null,
    having: String? = null,
    orderBy: String? = null
): Cursor =
    query(table, columns, selection, selectionArgs, groupBy, having, orderBy)
