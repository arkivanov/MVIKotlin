package com.arkivanov.mvikotlin.sample.todo.android.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.androidxlifecycleinterop.asMviLifecycle
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.statekeeper.StateKeeperProvider
import com.arkivanov.mvikotlin.core.statekeeper.retainInstance
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.android.FrameworkType
import com.arkivanov.mvikotlin.sample.todo.android.LifecycledConsumer
import com.arkivanov.mvikotlin.sample.todo.android.R
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.coroutines.controller.TodoListCoroutinesController
import com.arkivanov.mvikotlin.sample.todo.reaktive.controller.TodoListReaktiveController

class TodoListFragment(
    private val dependencies: Dependencies
) : Fragment(R.layout.todo_list), LifecycledConsumer<TodoListController.Input> {

    private val controller =
        dependencies
            .stateKeeperProvider
            .retainInstance(lifecycle = lifecycle.asMviLifecycle(), factory = ::createController)

    override val input: (TodoListController.Input) -> Unit = controller.input

    private fun createController(lifecycle: Lifecycle): TodoListController {
        val todoListControllerDependencies =
            object : TodoListController.Dependencies, Dependencies by dependencies {
                override val lifecycle: Lifecycle = lifecycle
            }

        return when (dependencies.frameworkType) {
            FrameworkType.REAKTIVE -> TodoListReaktiveController(todoListControllerDependencies)
            FrameworkType.COROUTINES -> TodoListCoroutinesController(todoListControllerDependencies)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller.onViewCreated(
            TodoListViewImpl(view),
            TodoAddViewImpl(view),
            viewLifecycleOwner.lifecycle.asMviLifecycle(),
            dependencies.listOutput
        )
    }

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val stateKeeperProvider: StateKeeperProvider<Any>
        val frameworkType: FrameworkType
        val listOutput: (TodoListController.Output) -> Unit
    }
}
