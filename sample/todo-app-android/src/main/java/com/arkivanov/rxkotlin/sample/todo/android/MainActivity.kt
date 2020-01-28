package com.arkivanov.rxkotlin.sample.todo.android

import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.rxkotlin.sample.todo.android.list.TodoListFragment

class MainActivity : AppCompatActivity() {

    private val fragmentFactory =
        FragmentFactoryImpl(
            context = this,
            storeFactory = storeFactory,
            todoListFragmentCallbacks = TodoListFragmentCallbacksImpl()
        )

    @IdRes
    private val contentId: Int = if (BuildConfig.DEBUG) R.id.content else android.R.id.content

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = fragmentFactory

        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            setContentView(R.layout.main_activity_debug)
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(contentId, fragmentFactory.todoListFragment())
                .commit()
        }
    }

    private inner class TodoListFragmentCallbacksImpl : TodoListFragment.Callbacks {
        override fun onItemSelected(id: String) {
        }
    }

    private class FragmentFactoryImpl(
        private val context: Context,
        private val storeFactory: StoreFactory,
        private val todoListFragmentCallbacks: TodoListFragment.Callbacks
    ) : FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
            when (loadFragmentClass(classLoader, className)) {
                TodoListFragment::class.java -> todoListFragment()
                else -> super.instantiate(classLoader, className)
            }

        fun todoListFragment(): TodoListFragment =
            TodoListFragment(
                database = context.app.database,
                storeFactory = storeFactory,
                callbacks = todoListFragmentCallbacks
            )
    }
}
