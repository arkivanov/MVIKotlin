package com.arkivanov.mvikotlin.core.rx

import com.arkivanov.mvikotlin.core.rx.AbstractSerializerThreadingTest

class SerializerThreadingTest : AbstractSerializerThreadingTest() {

    override val iterationCount: Int = 200000
}
