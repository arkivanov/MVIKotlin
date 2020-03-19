package com.arkivanov.mvikotlin.sample.todo.android.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.statekeeper.StateKeeperProvider
import com.arkivanov.mvikotlin.sample.todo.android.FrameworkType
import com.arkivanov.mvikotlin.sample.todo.android.R
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.coroutines.controller.TodoListCoroutinesController
import com.arkivanov.mvikotlin.sample.todo.reaktive.controller.TodoListReaktiveController

class TodoListFragment(
    database: TodoDatabase,
    storeFactory: StoreFactory,
    stateKeeperProvider: StateKeeperProvider<Any>,
    private val callbacks: Callbacks,
    frameworkType: FrameworkType
) : Fragment() {

    private val controller: TodoListController =
        when (frameworkType) {
            FrameworkType.REAKTIVE ->
                TodoListReaktiveController(
                    storeFactory = storeFactory,
                    stateKeeperProvider = stateKeeperProvider,
                    database = database
                )

            FrameworkType.COROUTINES ->
                TodoListCoroutinesController(
                    storeFactory = storeFactory,
                    stateKeeperProvider = stateKeeperProvider,
                    database = database
                )
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.todo_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller.onViewCreated(
            TodoListViewImpl(root = view, onItemSelected = callbacks::onItemSelected),
            TodoAddViewImpl(root = view)
        )
    }

    override fun onStart() {
        super.onStart()

        controller.onStart()
    }

    override fun onStop() {
        controller.onStop()

        super.onStop()
    }

    override fun onDestroyView() {
        controller.onViewDestroyed()

        super.onDestroyView()
    }

    override fun onDestroy() {
        controller.onDestroy()

        super.onDestroy()
    }

    interface Callbacks {
        fun onItemSelected(id: String)
    }
}
