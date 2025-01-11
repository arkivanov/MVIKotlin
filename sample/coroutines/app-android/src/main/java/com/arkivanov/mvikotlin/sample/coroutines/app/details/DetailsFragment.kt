package com.arkivanov.mvikotlin.sample.coroutines.app.details

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.arkivanov.essenty.lifecycle.essentyLifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.coroutines.app.R
import com.arkivanov.mvikotlin.sample.coroutines.shared.TodoDispatchers
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.DetailsController
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import kotlinx.parcelize.Parcelize

class DetailsFragment(
    private val storeFactory: StoreFactory,
    private val database: TodoDatabase,
    private val dispatchers: TodoDispatchers,
    private val onItemChanged: (id: String, data: TodoItem.Data) -> Unit,
    private val onItemDeleted: (id: String) -> Unit,
) : Fragment(R.layout.todo_details) {

    private lateinit var controller: DetailsController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        val args =
            requireArguments()
                .apply { classLoader = Arguments::class.java.classLoader }
                .getParcelable<Arguments>(KEY_ARGUMENTS) as Arguments

        controller =
            DetailsController(
                storeFactory = storeFactory,
                database = database,
                lifecycle = essentyLifecycle(),
                dispatchers = dispatchers,
                itemId = args.itemId,
                onItemChanged = onItemChanged,
                onItemDeleted = onItemDeleted,
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller.onViewCreated(DetailsViewImpl(view), viewLifecycleOwner.essentyLifecycle())
    }

    fun setArguments(itemId: String): DetailsFragment {
        arguments = bundleOf(KEY_ARGUMENTS to Arguments(itemId = itemId))

        return this
    }

    private companion object {
        private const val KEY_ARGUMENTS = "ARGUMENTS"
    }

    @Parcelize
    private class Arguments(val itemId: String) : Parcelable
}
