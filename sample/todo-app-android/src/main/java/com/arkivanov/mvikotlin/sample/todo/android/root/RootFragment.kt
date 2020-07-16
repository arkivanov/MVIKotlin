package com.arkivanov.mvikotlin.sample.todo.android.root

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.android.FrameworkType
import com.arkivanov.mvikotlin.sample.todo.android.R
import com.arkivanov.mvikotlin.sample.todo.android.Relay
import com.arkivanov.mvikotlin.sample.todo.android.details.TodoDetailsFragment
import com.arkivanov.mvikotlin.sample.todo.android.list.TodoListFragment
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController.Output as DetailsOutput
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Input as ListInput
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Output as ListOutput

class RootFragment(
    private val dependencies: Dependencies
) : Fragment(R.layout.content) {

    private val listInput = Relay<ListInput>()
    private val fragmentFactory = FragmentFactoryImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        childFragmentManager.fragmentFactory = fragmentFactory

        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager
                .beginTransaction()
                .add(R.id.content, fragmentFactory.todoListFragment())
                .commit()
        }
    }

    fun onBackPressed(): Boolean =
        if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
            true
        } else {
            false
        }

    private fun listOutput(output: ListOutput) {
        when (output) {
            is ListOutput.ItemSelected -> openDetails(itemId = output.id)
        }.let {}
    }

    private fun openDetails(itemId: String) {
        childFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_fade_in_bottom, R.anim.scale_fade_out, R.anim.scale_fade_in, R.anim.slide_fade_out_bottom)
            .replace(R.id.content, fragmentFactory.todoDetailsFragment().setArguments(itemId = itemId))
            .addToBackStack(null)
            .commit()
    }

    private fun detailsOutput(output: DetailsOutput) {
        when (output) {
            is DetailsOutput.Finished -> childFragmentManager.popBackStack()
            is DetailsOutput.ItemChanged -> listInput(ListInput.ItemChanged(id = output.id, data = output.data))
            is DetailsOutput.ItemDeleted -> listInput(ListInput.ItemDeleted(id = output.id))
        }.let {}
    }

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val frameworkType: FrameworkType
    }

    private inner class FragmentFactoryImpl : FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
            when (loadFragmentClass(classLoader, className)) {
                TodoListFragment::class.java -> todoListFragment()
                TodoDetailsFragment::class.java -> todoDetailsFragment()
                else -> super.instantiate(classLoader, className)
            }

        fun todoListFragment(): TodoListFragment =
            TodoListFragment(
                object : TodoListFragment.Dependencies, Dependencies by dependencies {
                    override val listOutput: (ListOutput) -> Unit = ::listOutput
                }
            ).also(listInput::subscribe)

        fun todoDetailsFragment(): TodoDetailsFragment =
            TodoDetailsFragment(
                object : TodoDetailsFragment.Dependencies, Dependencies by dependencies {
                    override val detailsOutput: DetailsOutput.() -> Unit = ::detailsOutput
                }
            )
    }
}
