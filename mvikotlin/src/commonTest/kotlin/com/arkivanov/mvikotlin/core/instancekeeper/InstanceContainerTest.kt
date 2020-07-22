package com.arkivanov.mvikotlin.core.instancekeeper

import com.arkivanov.mvikotlin.core.lifecycle.LifecycleRegistry
import kotlin.test.Test
import kotlin.test.assertSame

class InstanceContainerTest {

    @Test
    fun retains_instances() {
        val container1 = InstanceContainer(LifecycleRegistry())
        val data = Data()

        container1.get<Data>(key = "data").instance = data
        val retainedData = container1.get<Data>(key = "data").instance

        assertSame(data, retainedData)
    }

    class Data
}
