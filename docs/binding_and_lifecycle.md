[Overview](index.md) | [Store](store.md) | [View](view.md) | Binding and Lifecycle | [State preservation](state_preservation.md) | [Logging](logging.md) | [Time travel](time_travel.md)

## Binding

Connecting inputs and outputs sounds like a simple task. And indeed it is. But it can be even easier if you use [Binder](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/binder/Binder.kt). It provides just two methods: `start()` and `stop()`. When you call `start()` it connects (subscribes) outputs with inputs. And when you call `stop()` it disconnects (unsubscribes).

### Creating a Binder

Let's bind our `CalculatorStore` with `CalculatorView` which we created earlier.

First of all we will need to map `CalculatorStore.State` to `CalculatorView.Model`:

```kotlin
internal val stateToModel: CalculatorStore.State.() -> CalculatorView.Model =
    {
        CalculatorView.Model(
            value = value.toString()
        )
    }
```

We also need to map `CalculatorView.Event` to `CalculatorStore.Intent`:

```kotlin
internal val eventToIntent: CalculatorView.Event.() -> CalculatorStore.Intent =
    {
        when (this) {
            is CalculatorView.Event.IncrementClicked -> CalculatorStore.Intent.Increment
            is CalculatorView.Event.DecrementClicked -> CalculatorStore.Intent.Decrement
        }
    }
```

As mentioned earlier you can avoid separate `View Models` and `View Events` and just render `State` and/or produce `Intents`. In this case you will not need mappers, but you might get extra logic in your `Views`. In addition your `Stores` and `Views` will become coupled.

You can bind outputs with inputs using DSL provided by `mvikotlin-extensions-coroutines` and `mvikotlin-extensions-reaktive` modules:

```kotlin
class CalculatorController {
    private val store = CalculatorStoreFactory(DefaultStoreFactory).create()
    private var binder: Binder? = null

    fun onViewCreated(view: CalculatorView) {
        binder = bind {
            store.states.map(stateToModel) bindTo view
            view.events.map(eventToIntent) bindTo store
        }
    }

    fun onStart() {
        binder?.start()
    }

    fun onStop() {
        binder?.stop()
    }

    fun onViewDestroyed() {
        binder = null
    }
}
```

The controller is supposed to be used by platforms. We are creating the `Binder` in `onViewCreated(CalculatorView)` callback which is called by a platform when the `CalculatorView` is created. The `Binder` will bind `CalculatorStore` with `CalculatorView` in `onStart()` and will unbind them in `onStop()`.

Same way you can bind any outputs with any inputs. E.g. you can bind `Labels` of a `StoreA` with `Intents` of a `StoreB`, or `View Events` with an analytics tracker.

## Lifecycle

MVIKotlin provides an abstraction over [Lifecycle](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/lifecycle/Lifecycle.kt) states and events. Pleaser take a look at the following diagram:
![Lifecycle](media/lifecycle.jpg)

You can subscribe to `Lifecycle` events and unsubscribe from them. You can convert the [AndroidX Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle) to MVIKotlin `Lifecycle` using `androidx-lifecycle-interop` module. Also there is [LifecycleRegistry](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/lifecycle/LifecycleRegistry.kt) which implements both the `Lifecycle` and `Lifecycle.Callbacks` interfaces, so you can manually control it.

## Binder + Lifecycle

Work with the `Binder` can be simplified if you use the `Lifecycle`. Let's simplify our previous binding example::

```kotlin
class CalculatorController {
    private val store = CalculatorStoreFactory(DefaultStoreFactory).create()

    fun onViewCreated(view: CalculatorView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.START_STOP) {
            store.states.map(stateToModel) bindTo view
            view.events.map(eventToIntent) bindTo store
        }
    }
}
```

We passed the `viewLifecycle` together with the `CalculatorView` itself and used it for binding. Now `Binder` will automatically connect endpoints when started and disconnect when stopped.

Please refer to the [samples](https://github.com/arkivanov/MVIKotlin/tree/master/sample) for more examples.

[Overview](index.md) | [Store](store.md) | [View](view.md) | Binding and Lifecycle | [State preservation](state_preservation.md) | [Logging](logging.md) | [Time travel](time_travel.md)
