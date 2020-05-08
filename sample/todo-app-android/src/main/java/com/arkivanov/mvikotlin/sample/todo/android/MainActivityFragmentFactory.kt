package com.arkivanov.mvikotlin.sample.todo.android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.statekeeper.StateKeeperProvider
import com.arkivanov.mvikotlin.sample.todo.android.details.TodoDetailsFragment
import com.arkivanov.mvikotlin.sample.todo.android.list.TodoListFragment
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase

class MainActivityFragmentFactory(
    private val dependencies: Dependencies
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
        when (loadFragmentClass(classLoader, className)) {
            TodoListFragment::class.java -> todoListFragment()
            TodoDetailsFragment::class.java -> todoDetailsFragment()
            else -> super.instantiate(classLoader, className)
        }

    fun todoListFragment(): TodoListFragment =
        TodoListFragment(
            object : TodoListFragment.Dependencies, Dependencies by dependencies {
            }
        )

    fun todoDetailsFragment(): TodoDetailsFragment =
        TodoDetailsFragment(
            object : TodoDetailsFragment.Dependencies, Dependencies by dependencies {
            }
        )

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val stateKeeperProvider: StateKeeperProvider<Any>
        val frameworkType: FrameworkType
        val onItemSelectedListener: (id: String) -> Unit
        val onDetailsFinishedListener: () -> Unit
    }
}
