package com.arkivanov.mvikotlin.sample.todo.android

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.timetravel.TimeTravelSerializer
import com.arkivanov.mvikotlin.timetravel.controller.timeTravelController
import com.arkivanov.mvikotlin.timetravel.widget.TimeTravelView

class DebugDrawerFragment : Fragment() {

    private val serializer = TimeTravelSerializer()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        TimeTravelView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setOnExportEventsListener(::exportEvents)
            setOnImportEventsListener(::importEvents)
        }

    private fun exportEvents() {
        shareEvents(
            try {
                serializer.serialize(timeTravelController.state.events)
            } catch (e: Exception) {
                showError(R.string.time_travel_serialize_error, e)
                return
            }
        )
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
            timeTravelController.restoreEvents(
                try {
                    serializer.deserialize(text)
                } catch (e: Exception) {
                    showError(R.string.time_travel_deserialize_error, e)
                    return
                }
            )
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
