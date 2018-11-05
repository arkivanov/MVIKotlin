package com.arkivanov.mvidroid.store.timetravel

import com.arkivanov.mvidroid.store.MviEventType
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviReducer
import com.arkivanov.mvidroid.testutils.TestExecutor
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.observers.TestObserver
import org.junit.Assert

internal class MviTimeTravelStoreTestingEnvironment {

    val intentToAction = mock<(String) -> String>()
    val executorHolder = ExecutorHolder()
    val newExecutorHolder = ExecutorHolder()
    val reducer = mock<MviReducer<String, String>> {
        on { "state".reduce("result") }.thenReturn("new_state")
    }
    lateinit var store: MviTimeTravelStore<String, String, String, String, String>
    private lateinit var receivedEvents: TestObserver<Any>

    init {
        createStore()
    }

    fun createIntentEvent(value: String = "intent", state: String = "state"): MviTimeTravelEvent =
        MviTimeTravelEvent("store", MviEventType.INTENT, value, state)

    fun createActionEvent(value: String = "action", state: String = "state"): MviTimeTravelEvent =
        MviTimeTravelEvent("store", MviEventType.ACTION, value, state)

    fun createResultEvent(value: String = "result", state: String = "state"): MviTimeTravelEvent =
        MviTimeTravelEvent("store", MviEventType.RESULT, value, state)

    fun createStateEvent(value: String = "state", state: String = "state"): MviTimeTravelEvent =
        MviTimeTravelEvent("store", MviEventType.STATE, value, state)

    fun createLabelEvent(value: String = "label", state: String = "state"): MviTimeTravelEvent =
        MviTimeTravelEvent("store", MviEventType.LABEL, value, state)

    fun assertEvents(vararg events: MviTimeTravelEvent) {
        Assert.assertEquals(listOf(*events), receivedEvents.values())
    }

    private fun createStore(
        bootstrapper: MviBootstrapper<String>? = null,
        executorHolderFactory: () -> ExecutorHolder = mock { on { invoke() }.thenReturn(executorHolder, newExecutorHolder) }
    ) {
        store = MviTimeTravelStore(
            "store",
            "state",
            bootstrapper,
            intentToAction,
            { executorHolderFactory().executor },
            reducer
        )
        receivedEvents = TestObserver<Any>().also { store.events.subscribe(it) }
        store.init()
    }

    class ExecutorHolder {
        lateinit var stateSupplier: () -> String
        lateinit var resultConsumer: (String) -> Unit
        lateinit var labelConsumer: (String) -> Unit

        val executor = mock<TestExecutor> { _ ->
            on { init(any(), any(), any()) }.thenAnswer {
                stateSupplier = it.getArgument(0)
                resultConsumer = it.getArgument(1)
                labelConsumer = it.getArgument(2)
                Unit
            }
        }
    }
}