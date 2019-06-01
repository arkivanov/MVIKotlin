package com.arkivanov.mvidroid.sample.app.screen

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arkivanov.mvidroid.sample.app.R
import com.arkivanov.mvidroid.store.timetravel.MviTimeTravelController
import com.arkivanov.mvidroid.store.timetravel.MviTimeTravelSerializer
import com.arkivanov.mvidroid.utils.attachTo
import com.arkivanov.mvidroid.widget.MviTimeTravelView

class DebugDrawerFragment : Fragment() {

    private val controller = MviTimeTravelController
    private val serializer = MviTimeTravelSerializer()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        MviTimeTravelView(requireContext())
            .apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                setOnExportEventsListener(::exportEvents)
                setOnImportEventsListener(::importEvents)
            }

    private fun exportEvents() {
        serializer
            .serialize(controller.events)
            .subscribe(
                { shareEvents(it) },
                { showError(R.string.time_travel_serialize_error, it) }
            )
            .attachTo(lifecycle)
    }

    private fun shareEvents(data: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.time_travel_export_subject)
        intent.putExtra(android.content.Intent.EXTRA_TEXT, data)
        startActivity(Intent.createChooser(intent, getText(R.string.time_travel_export_chooser_title)))
    }

    private fun importEvents() {
        val text = getTextFromClipboard()
        if (text == null) {
            showError(R.string.time_travel_clipboard_empty)
        } else {
            serializer
                .deserialize(text)
                .subscribe(
                    { controller.restoreEvents(it) },
                    { showError(R.string.time_travel_deserialize_error, it) }
                )
                .attachTo(lifecycle)
        }
    }

    private fun getTextFromClipboard(): String? =
        (requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            .primaryClip
            ?.takeIf { it.itemCount > 0 }
            ?.getItemAt(0)
            ?.text
            ?.toString()

    private fun showError(@StringRes textId: Int, throwable: Throwable? = null) {
        Toast.makeText(context, textId, Toast.LENGTH_SHORT).show()

        if (throwable != null) {
            Log.e("MVIDroid", getString(textId), throwable)
        }
    }
}