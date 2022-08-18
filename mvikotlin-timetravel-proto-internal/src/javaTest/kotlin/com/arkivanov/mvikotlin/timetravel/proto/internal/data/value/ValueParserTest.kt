package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("TestFunctionName")
class ValueParserTest {

    @Test
    fun WHEN_parseValue_THEN_result_matches_original_object() {
        val node = ValueParser().parseValue(State())

        assertEquals(node, expectedValueNode())
    }

    private fun expectedValueNode(): ValueNode =
        ValueNode(
            name = null,
            type = "State",
            value = "{State}",
            children = listOf(
                ValueNode(name = "intValue", type = "Int", value = "5"),
                ValueNode(name = "longValue", type = "Long", value = "5"),
                ValueNode(name = "shortValue", type = "Short", value = "5"),
                ValueNode(name = "byteValue", type = "Byte", value = "5"),
                ValueNode(name = "floatValue", type = "Float", value = "5.0"),
                ValueNode(name = "doubleValue", type = "Double", value = "5.0"),
                ValueNode(name = "booleanValue", type = "Boolean", value = "true"),
                ValueNode(name = "stringValue", type = "String", value = "\"string\""),
                ValueNode(
                    name = "someEnum",
                    type = "SomeEnum",
                    value = "{SomeEnum}",
                    children = listOf(
                        ValueNode(name = "friendlyName", type = "String", value = "\"One\""),
                        ValueNode(name = "name", type = "String", value = "\"ONE\""),
                        ValueNode(name = "ordinal", type = "Int", value = "0"),
                    )
                ),
                ValueNode(
                    name = "child",
                    type = "Child",
                    value = "{Child}",
                    children = listOf(
                        ValueNode(name = "v2", type = "Int", value = "2"),
                        ValueNode(name = "v1", type = "Int", value = "1"),
                    )
                ),
                ValueNode(
                    name = "list",
                    type = "ArrayList<E>",
                    value = "{ArrayList<E>[3]}",
                    children = listOf(
                        ValueNode(name = "[0]", type = "String", value = "\"a\""),
                        ValueNode(name = "[1]", type = "Object", value = "null"),
                        ValueNode(name = "[2]", type = "Int", value = "{Int} 1"),
                    )
                ),
                ValueNode(
                    name = "map",
                    type = "LinkedHashMap<K, V>",
                    value = "{LinkedHashMap<K, V>[3]}",
                    children = listOf(
                        ValueNode(
                            name = null,
                            type = "\"a\" -> \"b\"",
                            value = null,
                            children = listOf(
                                ValueNode(name = "key", type = "String", value = "\"a\""),
                                ValueNode(name = "value", type = "String", value = "\"b\""),
                            )
                        ),
                        ValueNode(
                            name = null,
                            type = "null -> {Int} 1",
                            value = null,
                            children = listOf(
                                ValueNode(name = "key", type = "Object", value = "null"),
                                ValueNode(name = "value", type = "Int", value = "{Int} 1"),
                            )
                        ),
                        ValueNode(
                            name = null,
                            type = "{Int} 1 -> null",
                            value = null,
                            children = listOf(
                                ValueNode(name = "key", type = "Int", value = "{Int} 1"),
                                ValueNode(name = "value", type = "Object", value = "null"),
                            )
                        ),
                    )
                ),
                ValueNode(
                    name = "intArray",
                    type = "IntArray",
                    value = "{IntArray[2]}",
                    children = listOf(
                        ValueNode(type = "[0] = 1"),
                        ValueNode(type = "[1] = 2"),
                    )
                ),
                ValueNode(
                    name = "longArray",
                    type = "LongArray",
                    value = "{LongArray[2]}",
                    children = listOf(
                        ValueNode(type = "[0] = 1"),
                        ValueNode(type = "[1] = 2"),
                    )
                ),
                ValueNode(
                    name = "shortArray",
                    type = "ShortArray",
                    value = "{ShortArray[2]}",
                    children = listOf(
                        ValueNode(type = "[0] = 1"),
                        ValueNode(type = "[1] = 2"),
                    )
                ),
                ValueNode(
                    name = "byteArray",
                    type = "ByteArray",
                    value = "{ByteArray[2]}",
                    children = listOf(
                        ValueNode(type = "[0] = 1"),
                        ValueNode(type = "[1] = 2"),
                    )
                ),
                ValueNode(
                    name = "floatArray",
                    type = "FloatArray",
                    value = "{FloatArray[2]}",
                    children = listOf(
                        ValueNode(type = "[0] = 1.0"),
                        ValueNode(type = "[1] = 2.0"),
                    )
                ),
                ValueNode(
                    name = "doubleArray",
                    type = "DoubleArray",
                    value = "{DoubleArray[2]}",
                    children = listOf(
                        ValueNode(type = "[0] = 1.0"),
                        ValueNode(type = "[1] = 2.0"),
                    )
                ),
                ValueNode(
                    name = "charArray",
                    type = "CharArray",
                    value = "{CharArray[2]}",
                    children = listOf(
                        ValueNode(type = "[0] = a"),
                        ValueNode(type = "[1] = b"),
                    )
                ),
                ValueNode(
                    name = "booleanArray",
                    type = "BooleanArray",
                    value = "{BooleanArray[2]}",
                    children = listOf(
                        ValueNode(type = "[0] = true"),
                        ValueNode(type = "[1] = false"),
                    )
                ),
                ValueNode(
                    name = "stringArray",
                    type = "Array<String>",
                    value = "{Array<String>[2]}",
                    children = listOf(
                        ValueNode(name = "[0]", type = "String", value = "\"a\""),
                        ValueNode(name = "[1]", type = "Object", value = "null"),
                    )
                ),
                ValueNode(
                    name = "objectArray",
                    type = "Array<Any>",
                    value = "{Array<Any>[3]}",
                    children = listOf(
                        ValueNode(name = "[0]", type = "String", value = "\"a\""),
                        ValueNode(name = "[1]", type = "Object", value = "null"),
                        ValueNode(name = "[2]", type = "Int", value = "{Int} 1"),
                    )
                ),
            )
        )

    @Suppress("unused")
    class State(
        val intValue: Int = 5,
        val longValue: Long = 5L,
        val shortValue: Short = 5,
        val byteValue: Byte = 5,
        val floatValue: Float = 5f,
        val doubleValue: Double = 5.0,
        val booleanValue: Boolean = true,
        val stringValue: String = "string",
        val someEnum: SomeEnum = SomeEnum.ONE,
        val child: Child = Child(),
        val list: List<Any?> = listOf("a", null, 1),
        val map: Map<Any?, Any?> = mapOf("a" to "b", null to 1, 1 to null),
        val intArray: IntArray = intArrayOf(1, 2),
        val longArray: LongArray = longArrayOf(1L, 2L),
        val shortArray: ShortArray = shortArrayOf(1, 2),
        val byteArray: ByteArray = byteArrayOf(1, 2),
        val floatArray: FloatArray = floatArrayOf(1.0f, 2.0f),
        val doubleArray: DoubleArray = doubleArrayOf(1.0, 2.0),
        val charArray: CharArray = charArrayOf('a', 'b'),
        val booleanArray: BooleanArray = booleanArrayOf(true, false),
        val stringArray: Array<String?> = arrayOf("a", null),
        val objectArray: Array<Any?> = arrayOf("a", null, 1),
    )

    @Suppress("unused")
    open class Parent {
        val v1: Int = 1
    }

    @Suppress("unused")
    class Child : Parent() {
        val v2: Int = 2
    }

    @Suppress("unused")
    enum class SomeEnum(
        val friendlyName: String,
    ) {
        ONE("One"),
    }
}
