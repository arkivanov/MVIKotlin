[Overview](index.md) | [Store](store.md) | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | [State preservation](state_preservation.md) | Logging | [Time travel](time_travel.md)

## Logging

Logging is an essential tool for almost every app. MVIKotlin provides logging functionality via the [LoggingStoreFactory](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-logging/src/commonMain/kotlin/com/arkivanov/mvikotlin/logging/store/LoggingStoreFactory.kt) wrapper located in the `mvikotlin-logging` module. It is possible to replace the default `Logger` and `LogFormatter` with a custom ones.

> ⚠️ Logging is a debugging tool and may affect performance, ideally it should not be used in production.

### Using LoggingStoreFactory

Suppose we have the following `Store` factory:

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): CalculatorStore =
        object : CalculatorStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "CounterStore",
            // ...
        ) {
        }

    // ...
}
```

It accepts a `StoreFactory` and uses it to create an implementation of the `CalculatorStore`. You can now pass any `StoreFactory` here. So if you want to add logging just pass an instance of the `LoggingStoreFactory`:

```kotlin
val storeFactory = LoggingStoreFactory(DefaultStoreFactory)

CalculatorStoreFactory(storeFactory).create()
```

Normally you should define a global `StoreFactory` somewhere in the main app and pass it down to all the dependencies.

Please refer to the [samples](https://github.com/arkivanov/MVIKotlin/tree/master/sample) for more examples.

[Overview](index.md) | [Store](store.md) | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | [State preservation](state_preservation.md) | Logging | [Time travel](time_travel.md)
