package com.arkivanov.mvikotlin.extensions.androidx.statekeeper

import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.mvikotlin.core.statekeeper.StateKeeperProvider

/**
 * Creates a new instance of [StateKeeperProvider] and attaches it to the [AppCompatActivity].
 * All registered state suppliers will be automatically called and all collected state
 * will be retained over configuration change.
 * The Activity must be attached (its onCreate method is called) to the Application instance
 * when calling this method.
 */
fun AppCompatActivity.retainingStateKeeperProvider(): StateKeeperProvider<Any> =
    retainingStateKeeperProvider(::isChangingConfigurations)
