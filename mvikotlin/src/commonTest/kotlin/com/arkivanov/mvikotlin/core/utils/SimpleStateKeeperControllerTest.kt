package com.arkivanov.mvikotlin.core.utils

import com.arkivanov.mvikotlin.core.statekeeper.SimpleStateKeeperController
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleStateKeeperControllerTest {

    @Test
    fun saves_and_restores_states() {
        val controller = SimpleStateKeeperController { null }
        controller.get(String::class, "key1").register { "state1" }
        controller.get(String::class, "key2").register { "state2" }

        val map = HashMap<String, Any>()
        controller.save(map)

        val newController = SimpleStateKeeperController { map }
        val state1 = newController.get(String::class, "key1").getState()
        val state2 = newController.get(String::class, "key2").getState()

        assertEquals("state1", state1)
        assertEquals("state2", state2)
    }
}
