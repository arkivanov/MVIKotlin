package com.arkivanov.mvikotlin.sample.todo.android

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.android.root.RootFragment
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase

class MainActivity : AppCompatActivity() {

    private val fragmentFactory = MainActivityFragmentFactoryImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = fragmentFactory

        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, fragmentFactory.rootFragment())
                .commit()
        }
    }

    override fun onBackPressed() {
        supportFragmentManager
            .fragments
            .forEach {
                if ((it as? RootFragment)?.onBackPressed() == true) {
                    return
                }
            }

        super.onBackPressed()
    }

    private inner class MainActivityFragmentFactoryImpl : FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
            when (loadFragmentClass(classLoader, className)) {
                RootFragment::class.java -> rootFragment()
                else -> super.instantiate(classLoader, className)
            }

        fun rootFragment(): RootFragment =
            RootFragment(
                object : RootFragment.Dependencies {
                    override val storeFactory: StoreFactory get() = storeFactoryInstance
                    override val database: TodoDatabase get() = app.database
                    override val frameworkType: FrameworkType = FrameworkType.COROUTINES
                }
            )
    }
}
