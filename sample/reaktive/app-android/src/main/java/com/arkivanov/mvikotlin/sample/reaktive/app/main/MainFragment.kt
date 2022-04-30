package com.arkivanov.mvikotlin.sample.reaktive.app.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arkivanov.essenty.instancekeeper.instanceKeeper
import com.arkivanov.essenty.lifecycle.essentyLifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.arkivanov.mvikotlin.sample.reaktive.app.R
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.MainController

class MainFragment(
    private val storeFactory: StoreFactory,
    private val database: TodoDatabase,
    private val onItemSelected: (id: String) -> Unit,
) : Fragment(R.layout.todo_list) {

    private lateinit var controller: MainController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        controller =
            MainController(
                storeFactory = storeFactory,
                database = database,
                lifecycle = essentyLifecycle(),
                instanceKeeper = instanceKeeper(),
                onItemSelected = onItemSelected,
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller.onViewCreated(MainViewImpl(view), viewLifecycleOwner.essentyLifecycle())
    }

    fun onItemChanged(id: String, data: TodoItem.Data) {
        controller.onItemChanged(id = id, data = data)
    }

    fun onItemDeleted(id: String) {
        controller.onItemDeleted(id = id)
    }
}
