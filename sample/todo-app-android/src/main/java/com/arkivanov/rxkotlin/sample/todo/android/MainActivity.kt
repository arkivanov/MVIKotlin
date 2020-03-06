package com.arkivanov.rxkotlin.sample.todo.android

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.rxkotlin.sample.todo.android.details.TodoDetailsFragment
import com.arkivanov.rxkotlin.sample.todo.android.list.TodoListFragment

class MainActivity : AppCompatActivity() {

    private val fragmentFactory by lazy {
        FragmentFactoryImpl(
            database = app.database,
            storeFactory = storeFactory,
            frameworkType = FrameworkType.COROUTINES,
            todoListFragmentCallbacks = TodoListFragmentCallbacksImpl(),
            todoDetailsFragmentCallbacks = TodoDetailsFragmentCallbacksImpl()
        )
    }

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
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_fade_in_bottom, R.anim.scale_fade_out, R.anim.scale_fade_in, R.anim.slide_fade_out_bottom)
                .replace(contentId, fragmentFactory.todoDetailsFragment().setArguments(itemId = id))
                .addToBackStack(null)
                .commit()
        }
    }

    private inner class TodoDetailsFragmentCallbacksImpl : TodoDetailsFragment.Callbacks {
        override fun onFinished() {
            supportFragmentManager.popBackStack()
        }
    }

    private class FragmentFactoryImpl(
        private val database: TodoDatabase,
        private val storeFactory: StoreFactory,
        private val frameworkType: FrameworkType,
        private val todoListFragmentCallbacks: TodoListFragment.Callbacks,
        private val todoDetailsFragmentCallbacks: TodoDetailsFragment.Callbacks
    ) : FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
            when (loadFragmentClass(classLoader, className)) {
                TodoListFragment::class.java -> todoListFragment()
                TodoDetailsFragment::class.java -> todoDetailsFragment()
                else -> super.instantiate(classLoader, className)
            }

        fun todoListFragment(): TodoListFragment =
            TodoListFragment(
                database = database,
                storeFactory = storeFactory,
                callbacks = todoListFragmentCallbacks,
                frameworkType = frameworkType
            )

        fun todoDetailsFragment(): TodoDetailsFragment =
            TodoDetailsFragment(
                database = database,
                storeFactory = storeFactory,
                callbacks = todoDetailsFragmentCallbacks,
                frameworkType = frameworkType
            )
    }
}
