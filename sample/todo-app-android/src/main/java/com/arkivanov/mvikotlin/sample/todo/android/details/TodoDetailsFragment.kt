package com.arkivanov.mvikotlin.sample.todo.android.details

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.asMviLifecycle
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.android.FrameworkType
import com.arkivanov.mvikotlin.sample.todo.android.R
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.coroutines.controller.TodoDetailsCoroutinesController
import com.arkivanov.mvikotlin.sample.todo.reaktive.controller.TodoDetailsReaktiveController
import java.io.Serializable

class TodoDetailsFragment(
    private val dependencies: Dependencies
) : Fragment(R.layout.todo_details) {

    private lateinit var controller: TodoDetailsController
    private val args: Arguments by lazy { requireArguments().getSerializable(KEY_ARGUMENTS) as Arguments }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val todoDetailsControllerDependencies =
            object : TodoDetailsController.Dependencies, Dependencies by dependencies {
                override val lifecycle: Lifecycle = this@TodoDetailsFragment.lifecycle.asMviLifecycle()
                override val itemId: String = args.itemId
            }

        controller =
            when (dependencies.frameworkType) {
                FrameworkType.REAKTIVE -> TodoDetailsReaktiveController(todoDetailsControllerDependencies)
                FrameworkType.COROUTINES -> TodoDetailsCoroutinesController(todoDetailsControllerDependencies)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller.onViewCreated(TodoDetailsViewImpl(view), viewLifecycleOwner.lifecycle.asMviLifecycle())
    }

    fun setArguments(itemId: String): TodoDetailsFragment {
        arguments = bundleOf(KEY_ARGUMENTS to Arguments(itemId = itemId))

        return this
    }

    companion object {
        private const val KEY_ARGUMENTS = "ARGUMENTS"
    }

    private class Arguments(
        val itemId: String
    ) : Serializable // FIXME: Replace with parcelize

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val frameworkType: FrameworkType
        val detailsOutput: (TodoDetailsController.Output) -> Unit
    }
}
