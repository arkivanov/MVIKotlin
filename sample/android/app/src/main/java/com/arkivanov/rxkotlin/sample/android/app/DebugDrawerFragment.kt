package com.arkivanov.rxkotlin.sample.android.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.controller.timeTravelController
import com.arkivanov.mvikotlin.timetravel.widget.TimeTravelView

class DebugDrawerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        TimeTravelView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setOnExportEventsListener(::exportEvents)
            setOnImportEventsListener(::importEvents)
        }

    private fun exportEvents() {
    }

    private fun importEvents() {
        val events: List<TimeTravelEvent> = timeTravelController.state.events
    }
}
