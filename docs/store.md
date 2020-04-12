## Store

`Store` is the place for business logic. In MVIKotlin it is represented by the `Store` interface which is located in the `mvikotlin` module. You can check its definition [here](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/store/Store.kt).

It has the following features:
- There are three generic parameters: input `Intent` and output `State` and `Label`
- The property named `state` returns the current `State` of the `Store`
- Its `states(Observer<State>)` method is used to subscribe for `State` updates. When subscribed it emits the current `State` of the `Store`.
- The `labels(Observer<Label>)` method is used to subscribe for `Labels`
- The `accept(Intent)`  method supplies the `Store` with the `Intents`
- The `dispose()` method disposes the `Store` and cancels all its async operations

Every `Store` has up to three components: `Bootstrapper`, `Executor` and `Reducer`. Here is the diagram of how they are connected:

![Store](media/store.jpg)

### Bootstrapper

This component bootstraps (kick-starts) the `Store`. If passed to the `StoreFactory` it will be called at some point during `Store` creation. `Bootstrapper` produces `Actions` that are processed by the `Executor`.

### Executor

This is the place for business logic, all asynchronous operations also happen here. `Executor` accepts and processes `Intents` from the outside world and `Actions` from the `Bootstrapper`. Also the `Executor` has two outputs: `Results` and `Labels`. `Results` are passed to the `Reducer` and `Labels` are emitted straight to the outside world. `Executor` has constant access to a current `State`, a new `State` is visible for the `Executor` right after the `Result` is dispatched.

### Reducer

This component is basically a function that accepts a `Result` from the `Executor` and a current `State` of the `Store` and returns a new `State`. The `Reducer` is called for every `Result` produced by the `Executor` and the new `State` is applied and emitted as soon as the `Reducer` call returns.

## Creating a Store

Normally you don't need to implement the `Store` interface directly. Instead you should use [`StoreFactory`](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/store/StoreFactory.kt) which will create a `Store` for you. All you need to do is to provide up to three components (`Bootstrapper`, `Executor` and `Reducer`) and an initial `State`. `StoreFactory` is used to abstract from a `Store` implementation. We can use different factories depending on circumstances and combine them as needed.

There are a number of factories provided by MVIKotlin:
- [`DefaultStoreFactory`](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-main/src/commonMain/kotlin/com/arkivanov/mvikotlin/main/store/DefaultStoreFactory.kt) creates a default implementation of `Store` and is provided by the `mvikotlin-main` module.
- [`LoggingStoreFactory`](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-logging/src/commonMain/kotlin/com/arkivanov/mvikotlin/logging/store/LoggingStoreFactory.kt) wraps another `StoreFactory` and adds logging, it's provided by the `mvikotlin-logging` module.
- [`TimeTravelStoreFactory`](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-timetravel/src/commonMain/kotlin/com/arkivanov/mvikotlin/timetravel/store/TimeTravelStoreFactory.kt) is provided by the `mvikotlin-timetravel` module, it creates a `Store` with time travel functionality.

### Simplest example

Let's start from a very basic example. We will create a simple counter `Store` that will increment and decrement its value.

The first thing we should do is to define an interface. This is how it will look:

```kotlin
internal interface CalculatorStore : Store<Intent, State, Nothing> {

    sealed class Intent {
        object Increment : Intent()
        object Decrement : Intent()
    }

    data class State(
        val value: Long = 0L
    )
}
```

The `CalculatorStore` interface itself can be marked as `internal`, so it will be an mplementation detail of a module. Also `CalculatorStore` has two `Intents` (`Increment` and `Decrement`) and the `State` with just a `Long` value. This is the public API of our `Store`.

Now it's time for implementation:

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): CalculatorStore =
        object : CalculatorStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "CounterStore",
            initialState = State(),
            reducer = ReducerImpl
        ) {
        }

    private object ReducerImpl : Reducer<State, Intent> {
        override fun State.reduce(result: Intent): State =
            when (result) {
                is Intent.Increment -> copy(value = value + 1L)
                is Intent.Decrement -> copy(value = value - 1L)
            }
    }
}
```

The only component we need is the `Reducer`. It accepts `Intents` and modifies the `State` by incrementing or decrementing its value. The factory function `create()` uses the `StoreFactory` which is passed as a dependency.

### Adding Executor

Currently our `CalculatorStore` can only increment and decrement its value. But what if we need to calculate something? Let's say we want to calculate a sum of numbers from 1 to N. We will need an additional `Intent`:

```kotlin
internal interface CalculatorStore : Store<Intent, State, Nothing> {

    sealed class Intent {
        object Increment : Intent()
        object Decrement : Intent()
        data class Sum(val n: Int): Intent() // <-- Add this line
    }

    data class State(
        val value: Long = 0L
    )
}
```

The idea is that `CalculatorStore` will accept `Intent.Sum(N)`, calculate the sum of numbers from 1 to N and update the `State` with a result. But the calculation may take some time, so it should be performed in a background thread. For this we need the `Executor`.

So that our `Executor` could communicate with the `Reducer` we will need `Results`:

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    private sealed class Result {
        class Value(val value: Long) : Result()
    }
}
```

We will need a new `Reducer` because now it will accept `Results` instead of `Intents`:

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    private sealed class Result {
        class Value(val value: Long) : Result()
    }

    private object ReducerImpl : Reducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.Value -> copy(value = result.value)
            }
    }
}
```

There is only one possible `Result.Value(Long)` which just replaces whatever value in `State`.

Now it's time for the `Executor`. If you are interested you can find the interface [here](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/store/Executor.kt). Luckily we don't need to implement this entire interface. Instead we can extend a base implementation.

There are two base `Executors` provided by `MVIKotlin`:
- [`ReaktiveExecutor`](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-extensions-reaktive/src/commonMain/kotlin/com/arkivanov/mvikotlin/extensions/reaktive/ReaktiveExecutor.kt) - this implementation is based on the [Reaktive](https://github.com/badoo/Reaktive) library and is provided by `mvikotlin-extensions-reaktive` module
- [`SuspendExecutor`](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-extensions-coroutines/src/commonMain/kotlin/com/arkivanov/mvikotlin/extensions/coroutines/SuspendExecutor.kt) - this implementation is based on the [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) library and is provided by `mvikotlin-extensions-coroutines` module

Let's try both.

#### ReaktiveExecutor

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    // ...

    private class ExecutorImpl : ReaktiveExecutor<Intent, Nothing, State, Result, Nothing>() {
        override fun executeIntent(intent: Intent, getState: () -> State) =
            when (intent) {
                is Intent.Increment -> dispatch(Result.Value(getState().value + 1))
                is Intent.Decrement -> dispatch(Result.Value(getState().value - 1))
                is Intent.Sum -> sum(intent.n)
            }

        private fun sum(n: Int) {
            singleFromFunction { (1L..n.toLong()).sum() }
                .subscribeOn(computationScheduler)
                .map(Result::Value)
                .observeOn(mainScheduler)
                .subscribeScoped(onSuccess = ::dispatch)
        }
    }

    // ...
}
```

So we extended the `ReaktiveExecutor` class and implemented the `executeIntent` method. This method gives us an `Intent` and supplier of a current `State` supplier. For `Intent.Increment` and `Intent.Decrement` we simply send the `Result` with a new value using the `dispatch` method. But for `Intent.Sum` we use `Reaktive` for multithreading. We calculate the sum on the `computationScheduler` and then switch to the `mainScheduler` and `dispatch` the `Result`.

> ⚠️ `ReaktiveExecutor` implements Reaktive's [`DisposableScope`](https://github.com/badoo/Reaktive#subscription-management-with-disposablescope) which provides a bunch of additional extension functions. We used one of those functions - `subscribeScoped`. This ensures that the subscription is disposed when the `Store` (and so the `Executor`) is disposed.

#### SuspendExecutor

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    // ...

    private class ExecutorImpl : SuspendExecutor<Intent, Nothing, State, Result, Nothing>() {
        override suspend fun executeIntent(intent: Intent, getState: () -> State) =
            when (intent) {
                is Intent.Increment -> dispatch(Result.Value(getState().value + 1))
                is Intent.Decrement -> dispatch(Result.Value(getState().value - 1))
                is Intent.Sum -> sum(intent.n)
            }

        private suspend fun sum(n: Int) {
            val sum = withContext(Dispatchers.Default) { (1L..n.toLong()).sum() }
            dispatch(Result.Value(sum))
        }
    }

    // ...
}
```

Here we extended the `SuspendExecutor` class. This gives us the `suspend fun executeIntent` method so we can use coroutines. The sum is calculated on the `Default` dispatcher and the `Result` is dispatched from on the `Main` thread.

#### Creating the Store

We also need to pass a factory of our `Executor` to the `StoreFactory`:

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): CalculatorStore =
        object : CalculatorStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "CounterStore",
            initialState = State(),
            executorFactory = ::ExecutorImpl, // <-- Pass Executor factory
            reducer = ReducerImpl
        ) {
        }

    // ...
}
```

Why factory and not just an instance of the `Executor`? Because of the time travel feature. When debugging time travel events it creates separate instances of `Executors` when necessary and fakes their `States`.

### Adding Bootstrapper

When we create a new instance of a `Store` it will stay in an initial `State` and do nothing until you supply an `Intent`. But sometimes we need to bootstrap (or kick start) a `Store` so it will start doing something once created. E.g. it can start listening for events from a server or load something from a database. This is why we need the `Bootstrapper`. As mentioned in the beginning the `Bootstrapper` produces `Actions` that are processed by the `Executor` the same way as `Intents`.

Our `CalculatorStore` is able to calculate sums of numbers from 1 to N. Currently it does this when `Intent.Sum(N)` is received. Let's use the `Bootstrapper` to calculate `sum(100)` when the `CalculatorStore` is created. Our `Executor` already has everything to for sum calculation, so we can just send a triggering `Action` to the `Executor`, same as `Intent.Sum(N)`.

Let's first add an `Action`:

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    // ...

    private sealed class Action {
        class Sum(val n: Int): Action()
    }

    // ...
}
```

Not it's time to handle the `Action` in the `ReaktiveExecutor`:

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    // ...

    private class ExecutorImpl : ReaktiveExecutor<Intent, Action, State, Result, Nothing>() {
        override fun executeAction(action: Action, getState: () -> State) =
            when (action) {
                is Action.Sum -> sum(action.n)
            }

        // ...
    }

    // ...
}
```

And same for the `SuspendExecutor`:

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    // ...

    private class ExecutorImpl : SuspendExecutor<Intent, Action, State, Result, Nothing>() {
        override suspend fun executeAction(action: Action, getState: () -> State) =
            when (action) {
                is Action.Sum -> sum(action.n)
            }

        // ...
    }

    // ...
}
```

The only thing is missing is we need to somehow trigger the `Action`. We need to pass a `Bootstrapper` to the `StoreFactory`. For such a simple case we can use ['SimpleBootstrapper`](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/store/SimpleBootstrapper.kt):

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): CalculatorStore =
        object : CalculatorStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "CounterStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Action.Sum(100)), // <-- Add this line
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {
        }

    // ...
}
```

The `SimpleBootstrapper` just dispatches the provided `Actions`. But sometimes we need more, e.g. do some background work:

Using `ReaktiveBootstrapper` from the `mvikotlin-extensions-reaktive` module:

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): CalculatorStore =
        object : CalculatorStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "CounterStore",
            initialState = State(),
            bootstrapper = BootstrapperImpl, // <-- Pass BootstrapperImpl to the StoreFactory
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {
        }

    private sealed class Action {
        class SetValue(val value: Long): Action() // <-- Use another Action
    }
    
    // ...

    private object BootstrapperImpl : ReaktiveBootstrapper<Action>() {
        override fun invoke() {
            singleFromFunction { (1L..1000000.toLong()).sum() }
                .subscribeOn(computationScheduler)
                .map(Action::SetValue)
                .observeOn(mainScheduler)
                .subscribeScoped(onSuccess = ::dispatch)
        }
    }

    private class ExecutorImpl : ReaktiveExecutor<Intent, Action, State, Result, Nothing>() {
        override fun executeAction(action: Action, getState: () -> State) =
            when (action) {
                is Action.SetValue -> dispatch(Result.Value(action.value)) // <-- Handle the Action
            }

        // ...
    }

    // ...
}
```

Using `SuspendBootstrapper` from the `mvikotlin-extensions-coroutines` module:

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): CalculatorStore =
        object : CalculatorStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "CounterStore",
            initialState = State(),
            bootstrapper = BootstrapperImpl,
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {
        }

    private sealed class Action {
        class SetValue(val value: Long): Action()
    }

    // ...

    private object BootstrapperImpl : SuspendBootstrapper<Action>() {
        override suspend fun bootstrap() {
            val sum = withContext(Dispatchers.Default) { (1L..1000000.toLong()).sum() }
            dispatch(Action.SetValue(sum))
        }
    }

    private class ExecutorImpl : SuspendExecutor<Intent, Action, State, Result, Nothing>() {
        override suspend fun executeAction(action: Action, getState: () -> State) =
            when (action) {
                is Action.SetValue -> dispatch(Result.Value(action.value))
            }

        // ...
    }

    // ...
}
```
