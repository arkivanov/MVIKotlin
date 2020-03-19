package com.arkivanov.mvikotlin.sample.todo.android

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.statekeeper.SimpleStateKeeperContainer
import com.arkivanov.mvikotlin.core.utils.statekeeper.StateKeeperProvider
import com.arkivanov.mvikotlin.core.utils.statekeeper.saveAndGet
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase

class MainActivity : AppCompatActivity() {

    private val nonConfigurationStateKeeperContainer = SimpleStateKeeperContainer()
    private lateinit var fragmentFactory: MainActivityFragmentFactory

    @IdRes
    private val contentId: Int = if (BuildConfig.DEBUG) R.id.content else android.R.id.content

    override fun onCreate(savedInstanceState: Bundle?) {
        fragmentFactory = MainActivityFragmentFactory(MainActivityFragmentFactoryDependencies())
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

    override fun onRetainCustomNonConfigurationInstance(): Any? = nonConfigurationStateKeeperContainer.saveAndGet(HashMap())

    private inner class MainActivityFragmentFactoryDependencies : MainActivityFragmentFactory.Dependencies {
        override val storeFactory: StoreFactory get() = storeFactoryInstance
        override val database: TodoDatabase get() = app.database

        @Suppress("UNCHECKED_CAST")
        override val stateKeeperProvider: StateKeeperProvider<Any> =
            nonConfigurationStateKeeperContainer.getProvider(
                savedState = lastCustomNonConfigurationInstance as MutableMap<String, Any>?
            )

        override val frameworkType: FrameworkType = FrameworkType.COROUTINES
        override val onItemSelectedListener: (id: String) -> Unit = ::onItemSelected
        override val onDetailsFinishedListener: () -> Unit = ::onDetailsFinished

        private fun onItemSelected(id: String) {
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_fade_in_bottom, R.anim.scale_fade_out, R.anim.scale_fade_in, R.anim.slide_fade_out_bottom)
                .replace(contentId, fragmentFactory.todoDetailsFragment().setArguments(itemId = id))
                .addToBackStack(null)
                .commit()
        }

        private fun onDetailsFinished() {
            supportFragmentManager.popBackStack()
        }
    }
}
