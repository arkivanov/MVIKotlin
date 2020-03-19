package com.arkivanov.mvikotlin.sample.todo.android.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.coroutines.controller.TodoDetailsCoroutinesController
import com.arkivanov.mvikotlin.sample.todo.reaktive.controller.TodoDetailsReaktiveController
import com.arkivanov.mvikotlin.sample.todo.android.FrameworkType
import com.arkivanov.mvikotlin.sample.todo.android.R
import java.io.Serializable

class TodoDetailsFragment(
    private val database: TodoDatabase,
    private val storeFactory: StoreFactory,
    private val callbacks: Callbacks,
    private val frameworkType: FrameworkType
) : Fragment() {

    private lateinit var controller: TodoDetailsController
    private val args: Arguments by lazy { requireArguments().getSerializable(KEY_ARGUMENTS) as Arguments }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        controller =
            when (frameworkType) {
                FrameworkType.REAKTIVE ->
                    TodoDetailsReaktiveController(
                        storeFactory = storeFactory,
                        database = database,
                        itemId = args.itemId
                    )

                FrameworkType.COROUTINES -> TodoDetailsCoroutinesController(
                    storeFactory = storeFactory,
                    database = database,
                    itemId = args.itemId
                )
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.todo_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller.onViewCreated(TodoDetailsViewImpl(root = view, onFinished = callbacks::onFinished))
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

    interface Callbacks {
        fun onFinished()
    }
}
