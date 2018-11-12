## Store

`Store` is the place for business logic, the place where all magic happens.

In MVIDroid `Store` is represented by `MviStore` interface:
```kotlin
interface MviStore<out State : Any, in Intent : Any, out Label : Any> : Disposable {

    @get:MainThread
    val state: State

    val states: Observable<out State>
    val labels: Observable<out Label>

    @MainThread
    fun accept(intent: Intent)

    @MainThread
    override fun dispose()

    @MainThread
    override fun isDisposed(): Boolean
}
```

`Store` accepts `Intents` as input and produces `States` and `Labels` as output.
It also holds current `State` so you can access it at any time.
Also `Store` can be disposed so all running asynchronous operations will
be cancelled (`MviStore` interface extends `Disposable` interface).

![Store](media/store.jpg)

Let's take a closer look at inputs and outputs of `Store`:
* `Intents` can be considered as call to action and
usually represented as sealed class
* `State` describes current state of `Store` at any point of time and
usually represented as data class
* `Labels` are used for communication between `Stores`, `Label` of one
`Store` can be converted to `Intent` of another `Store`.
They are also usually a sealed classes.

The good news is that you don't need to implement this interface.
Instead you should use `MviStoreFactory` to create a `Store`.
But it's a good practice to define an interface for your `Store`.

Store can have up to three components:
1. `Executor`: every `Intent` is converted to an `Action` that is executed by
`Executor`. It processes all `Actions` and produces `Results` and `Labels`.
2. `Reducer`: every `Result` produced by `Executor` is going to `Reducer`
which is basically a function of (State, Result) -> State.
It accepts current `State` and an `Action` and produces a new `State`.
3. `Bootstrapper`: produces additional `Actions` for `Executor`,
it's a good place to subscribe to e.g. data source or to just produce
an initial `Action` so `Executor` will start doing something.

The simplest implementation of `Store` would be:

Interface:
```kotlin
interface DemoStore : MviStore<State, Intent, Nothing> {

    data class State(
        val data: String? = null
    )

    sealed class Intent {
        class ApplyString(val str: String?) : Intent()
    }
}
```

Factory:
```kotlin
class DemoStoreFactory(
    private val factory: MviStoreFactory
) {

    fun create(): DemoStore =
        object : MviStore<State, Intent, Nothing> by factory.createExecutorless(
            name = "DemoStore",
            initialState = State(),
            reducer = Reducer
        ), DemoStore {
        }

    private object Reducer : MviReducer<State, Intent> {
        override fun State.reduce(result: Intent): State =
            when (result) {
                is Intent.ApplyString -> copy(data = result.str)
            }
    }
}
```

As you can see this `Store` is very simple. It contains only `Reducer`
which means `Intents` are passed directly to the `Reducer`
(`Intents`, `Actions` and `Results` are of the same type). And it
does not produce any Labels (type of Labels is Nothing)
`Reducer` applies `Intents` and produces new `States`.

But what if we need to do some extra job? Let's say we want to accept
only those strings whose length is greater than 10.
It's time to use `Executor`.

```kotlin
class DemoStoreFactory(
    private val factory: MviStoreFactory
) {

    fun create(): DemoStore =
        object : MviStore<State, Intent, Nothing> by factory.createActionless(
            name = "DemoStore",
            initialState = State(),
            executorFactory = ::Executor,
            reducer = Reducer
        ), DemoStore {
        }

    private sealed class Result {
        class Str(val data: String) : Result()
    }

    private class Executor : MviExecutor<State, Intent, Result, Nothing>() {
        override fun execute(action: Intent): Disposable? =
            when (action) {
                is Intent.ApplyString -> processString(action.str)
            }

        private fun processString(str: String?): Disposable? {
            str
                ?.takeIf { it.length > 10 }
                ?.let(Result::Str)
                ?.also(::dispatch)

            return null
        }
    }

    private object Reducer : MviReducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.Str -> copy(data = result.data)
            }
    }
}
```

Our interface is still the same but factory is slightly different.
We have added an `Executor` and there is a separate type for `Results`.
Now `Executor` accepts `Intent`, checks that string is valid and
produces `Result` indicating that we have got a new string.
And as always `Reducer` applies `Result` to a `State` producing a new `State`.

Now let's try to add some async stuff: we will inject external string
processor into our `Store` and call it for every incoming string.

```kotlin
interface DemoStore : MviStore<State, Intent, Nothing> {

    data class State(
        val isProcessing: Boolean = false,
        val data: String? = null
    )

    ...
}

class DemoStoreFactory(
    private val factory: MviStoreFactory,
    private val stringProcessor: (String) -> Single<out String>
) {

    fun create(): DemoStore = ...

    private sealed class Result {
        object Processing : Result()
        class Data(val data: String) : Result()
    }

    private inner class Executor : MviExecutor<State, Intent, Result, Nothing>() {
        override fun execute(action: Intent): Disposable? =
            when (action) {
                is Intent.SetData ->
                    action
                        .data
                        ?.takeIf { it.length > 10 }
                        ?.let {
                            dispatch(Result.Processing)
                            stringProcessor(it)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { str ->
                                    dispatch(Result.Data(str))
                                }
                        }
            }
    }

    private object Reducer : MviReducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.Processing -> copy(isProcessing = true)
                is Result.Data -> copy(isProcessing = false, data = result.data)
            }
    }
}
```

We have added `isProcessing` flag to our `State` which indicates
whether processing is in progress or not. When `Executor` receives a string
with more than 10 characters, it first dispatches `Result.Processing`
and `Reducer` sets `isProcessing` flag to true. After that
`Executor` calls string processor and subscribes to it. Result string
is mapped to `Result.Data` and `Reducer` then clears `isProcessing` flag
and applies received string.

What if we want to start some job right after `Store` was created? In our
case we might want to initialize the `Store` with some initial string.
Welcome `Bootstrapper`!

```kotlin
class DemoStoreFactory(
    private val factory: MviStoreFactory,
    private val stringProcessor: (String) -> Single<out String>
) {

    fun create(): DemoStore =
        object : MviStore<State, Intent, Nothing> by factory.create(
            ...
            intentToAction = Action::ExecuteIntent,
            bootstrapper = MviSimpleBootstrapper(Action.Init),
            ...
        ), DemoStore {
        }

    private sealed class Action {
        object Init : Action()
        class ExecuteIntent(val intent: Intent) : Action()
    }

    private sealed class Result {
        object Processing : Result()
        class Data(val data: String) : Result()
    }

    private inner class Executor : MviExecutor<State, Action, Result, Nothing>() {
        override fun execute(action: Action): Disposable? =
            when (action) {
                is Action.Init -> processString("initial_string")
                is Action.ExecuteIntent -> executeIntent(action.intent)
            }

        private fun executeIntent(intent: Intent): Disposable? =
            when (intent) {
                is Intent.ApplyString -> processString(intent.str)
            }

        private fun processString(str: String?): Disposable? =
            str
                ?.takeIf { it.length > 10 }
                ?.let {
                    dispatch(Result.Processing)
                    stringProcessor(it)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { str ->
                            dispatch(Result.Data(str))
                        }
                }
    }

    private object Reducer : MviReducer<State, Result> {
        ...
    }
}
```

We need `Actions` when using `Bootstrapper` (`Intent` is a call to action
for `Store` and `Action` is a call to action for `Executor`). There is a
special mapper from `Intents` to `Actions` that we need to pass to
`Store Factory` along with `Bootstrapper`. We have added `Actions`
sealed classes and changed our `Executor` so it now accepts `Actions`
instead of `Intents`. We used `MviSimpleBootstrapper` which just
dispatches `Actions` passed to it, but you can implement `MviBootstrapper`
interface on your own if need some extra logic (e.g. subscribe to a
data source). Now `Bootstrapper` will dispatch `Action.Init` when `Store`
is created and it will be automatically process an initial string.

And a few words about `Labels`: they are special one-shot events
produced by `Store` and used for inter-store communication. To produce a
`Label` you simply call `publish(Label)` method in `Executor`, similar
to `dispatch(Result)`.

```kotlin
interface DemoStore : MviStore<State, Intent, Label> {

    ...

    sealed class Label {
        class NewString(val str: String) : Label()
    }
}

class DemoStoreFactory(
    private val factory: MviStoreFactory,
    private val stringProcessor: (String) -> Single<out String>
) {

    fun create(): DemoStore = ...

    private sealed class Action {
        ...
    }

    private sealed class Result {
        ...
    }

    private inner class Executor : MviExecutor<State, Action, Result, Label>() {
        ...

        private fun processString(str: String?): Disposable? =
            str
                ?.takeIf { it.length > 10 }
                ?.let {
                    dispatch(Result.Processing)
                    stringProcessor(it)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { str ->
                            dispatch(Result.Data(str))
                            publish(Label.NewString(str))
                        }
                }
    }

    private object Reducer : MviReducer<State, Result> {
        ...
    }
}
```

We have defined `Label` sealed class in `Store` interface and published
`Label.NewString` in `Executor`.

Some best practices to follow:
* Prefer single responsibility for `Stores` - many small `Stores` are
better than one big "God" `Store`
* Always define interfaces for `Stores` so their public contracts are clear
* Make everything inside `Store` factory `private`,
except factory method itself
* `Reducers` should always be pure functions
* When writing unit tests for `Stores` always test their public contracts
and never internal components
* When using DI frameworks it might be useful to make `Store` factory
implement "javax.inject.Provider" interface so you can easily bind
`Store` interface with its provider

---
[Index](index.md) [Next](component.md)
