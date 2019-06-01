package com.arkivanov.mvidroid.store.timetravel

import com.arkivanov.mvidroid.store.MviEventType
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class MviTimeTravelSerializerTest {

    private val serializer = MviTimeTravelSerializer()

    @Before
    fun before() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    fun after() {
        RxAndroidPlugins.reset()
    }

    @Test
    fun test_serialize_deserialize() {
        val events =
            MviTimeTravelEvents(
                items = listOf(
                    MviTimeTravelEvent(
                        storeName = "store1",
                        type = MviEventType.INTENT,
                        value = 100,
                        state = "state1"
                    ),
                    MviTimeTravelEvent(
                        storeName = "store2",
                        type = MviEventType.STATE,
                        value = 200,
                        state = "state2"
                    )
                ),
                index = 1
            )

        val result =
            serializer
                .serialize(events)
                .flatMap(serializer::deserialize)
                .blockingGet()

        assertEquals(events, result)
    }
}