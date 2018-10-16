package com.arkivanov.mvidroid.store.factory.logging

import com.arkivanov.mvidroid.store.MviEventType
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import com.arkivanov.mvidroid.store.factory.MviStoreFactory
import com.arkivanov.mvidroid.utils.DeepStringMode
import com.arkivanov.mvidroid.utils.logger.MviDefaultLogger
import com.arkivanov.mvidroid.utils.logger.MviLogger
import com.arkivanov.mvidroid.utils.toDeepString
import io.reactivex.disposables.Disposable

/**
 * An implementation of [MviStoreFactory] that wraps another Store factory and provides logging
 *
 * @param delegate an instance of another factory that will be wrapped by this factory
 * @param logger A logger that can be used to implement custom logging. By default [MviDefaultLogger] is used.
 * @param mode logging mode, see [MviLoggingStoreFactory.Mode] for more information
 */
class MviLoggingStoreFactory(
    private val delegate: MviStoreFactory,
    private val logger: MviLogger = MviDefaultLogger,
    var mode: Mode = Mode.MEDIUM
) : MviStoreFactory {

    override fun <State : Any, Intent : Any, Action : Any, Result : Any, Label : Any> create(
        name: String,
        initialState: State,
        bootstrapper: MviBootstrapper<Action>?,
        intentToAction: (Intent) -> Action,
        executorFactory: () -> MviExecutor<State, Action, Result, Label>,
        reducer: MviReducer<State, Result>
    ): MviStore<State, Intent, Label> {
        if (mode !== Mode.DISABLED) {
            logger.log("Store created: $name")
        }

        val delegate = delegate.create(
            name = name,
            initialState = initialState,
            bootstrapper = bootstrapper?.wrap(name),
            intentToAction = { intent: Intent ->
                intentToAction(intent).also { logEvent(name, MviEventType.ACTION, it) }
            },
            executorFactory = { executorFactory().wrap(name) },
            reducer = reducer.wrap(name)
        )

        return object : MviStore<State, Intent, Label> by delegate {
            override fun invoke(intent: Intent) {
                logEvent(name, MviEventType.INTENT, intent)
                delegate(intent)
            }

            override fun dispose() {
                delegate.dispose()

                if (mode !== Mode.DISABLED) {
                    logger.log("Store disposed: $name")
                }
            }
        }
    }

    private fun <Action : Any> MviBootstrapper<Action>.wrap(storeName: String): MviBootstrapper<Action> =
        object : MviBootstrapper<Action> {
            override fun bootstrap(dispatch: (Action) -> Unit): Disposable? =
                this@wrap.bootstrap {
                    logEvent(storeName, MviEventType.ACTION, it)
                    dispatch(it)
                }
        }

    private fun <State : Any, Action : Any, Result : Any, Label : Any> MviExecutor<State, Action, Result, Label>.wrap(
        storeName: String
    ): MviExecutor<State, Action, Result, Label> =
        object : MviExecutor<State, Action, Result, Label>() {
            init {
                this@wrap.init(
                    ::state,
                    {
                        logEvent(storeName, MviEventType.RESULT, it)
                        dispatch(it)
                    },
                    {
                        logEvent(storeName, MviEventType.LABEL, it)
                        publish(it)
                    }
                )
            }

            override fun execute(action: Action): Disposable? = this@wrap.execute(action)
        }

    private fun <State : Any, Result : Any> MviReducer<State, Result>.wrap(
        storeName: String
    ): MviReducer<State, Result> =
        object : MviReducer<State, Result> {
            override fun State.reduce(result: Result): State =
                with(this@wrap) { reduce(result) }
                    .also { logEvent(storeName, MviEventType.STATE, it) }
        }

    private fun logEvent(storeName: String, eventType: MviEventType, value: Any) {
        getDeepStringMode()?.also {
            logger.log("$storeName ($eventType, ${value::class.java.simpleName}): ${value.toDeepString(it, false)}")
        }
    }

    private fun getDeepStringMode(): DeepStringMode? =
        when (mode) {
            Mode.DISABLED -> null
            Mode.SHORT -> DeepStringMode.SHORT
            Mode.MEDIUM -> DeepStringMode.MEDIUM
            Mode.FULL -> DeepStringMode.FULL
        }

    /**
     * Defines verboseness of logging
     * * DISABLED - logging is disabled
     * * SHORT - strings are truncated to 64 symbols, items of arrays and collections are not printed
     * * MEDIUM - number of items of arrays and collections is limited to 10
     * * FULL - no limitations, everything is printed
     */
    enum class Mode {
        DISABLED, SHORT, MEDIUM, FULL
    }
}
