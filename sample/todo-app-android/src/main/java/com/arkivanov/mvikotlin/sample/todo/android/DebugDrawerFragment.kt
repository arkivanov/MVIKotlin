package com.arkivanov.mvikotlin.sample.todo.android

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.timetravel.controller.timeTravelController
import com.arkivanov.mvikotlin.timetravel.export.DefaultTimeTravelExportSerializer
import com.arkivanov.mvikotlin.timetravel.export.TimeTravelExportSerializer
import com.arkivanov.mvikotlin.timetravel.widget.TimeTravelView

class DebugDrawerFragment : Fragment() {

    private val serializer = DefaultTimeTravelExportSerializer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        TimeTravelView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setOnExportEventsListener(::exportEvents)
            setOnImportEventsListener(::importEvents)
        }

    private fun exportEvents() {
        when (val result = serializer.serialize(timeTravelController.export())) {
            is TimeTravelExportSerializer.Result.Success -> shareEvents(Base64.encodeToString(result.data, Base64.DEFAULT))
            is TimeTravelExportSerializer.Result.Error -> showError(R.string.time_travel_serialize_error, result.exception)
        }.let {}
    }

    private fun shareEvents(data: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, R.string.time_travel_export_subject)
        intent.putExtra(Intent.EXTRA_TEXT, data)
        startActivity(Intent.createChooser(intent, getText(R.string.time_travel_export_chooser_title)))
    }

    private fun importEvents() {
        val text = getTextFromClipboard()
        if (text == null) {
            showError(R.string.time_travel_clipboard_empty)
        } else {
            when (val result = serializer.deserialize(Base64.decode(text, Base64.DEFAULT))) {
                is TimeTravelExportSerializer.Result.Success -> timeTravelController.import(result.data)
                is TimeTravelExportSerializer.Result.Error -> showError(R.string.time_travel_deserialize_error, result.exception)
            }.let {}
        }
    }

    private fun getTextFromClipboard(): String? =
        (requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            .primaryClip
            ?.takeIf { it.itemCount > 0 }
            ?.getItemAt(0)
            ?.text
            ?.toString()

    private fun showError(@StringRes textId: Int, exception: Exception? = null) {
        Toast.makeText(context, textId, Toast.LENGTH_SHORT).show()

        if (exception != null) {
            Log.e("MviKotlin", getString(textId), exception)
        }
    }
}
