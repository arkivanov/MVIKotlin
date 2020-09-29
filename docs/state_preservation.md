[Overview](index.md) | [Store](store.md) | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | State preservation | [Logging](logging.md) | [Time travel](time_travel.md)

## State preservation

Sometimes it might be necessary to preserve a state (e.g. a state of a `Store`) in order to restore it later. A very common use case is Android Activity recreation due to system constraints. MVIKotlin provides a utility for state preservation - the `StateKeeper`.

The `StateKeeper` is a utility for state (or any other data) preservation. In general it does not make any assumptions on what is being preserved and how. The `StateKeeper` is provided by the `mvikotlin` module.

There are several interfaces related to this.

- [StateKeeper](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/statekeeper/StateKeeper.kt) - this generic interface is used to retrieve saved data, if any, and to register a data supplier. The data supplier is called when it's time to save the state. The generic type parameter ensures that types of the saved and restored data are same;
- [StateKeeperProvider](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/statekeeper/StateKeeperProvider.kt) - this generic interface provides typed instances of the `StateKeeper`;

There are extensions for AndroidX provided by `mvikotlin-extensions-androidx` module, can be used in `Fragments` and `Activities`:
- [getParcelableStateKeeperProvider()](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-extensions-androidx/src/androidMain/kotlin/com/arkivanov/mvikotlin/extensions/androidx/statekeeper/ParcelableStateKeeperProvider.kt) - preserves `Parcelable` data;
- [getSerializableStateKeeperProvider()](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-extensions-androidx/src/androidMain/kotlin/com/arkivanov/mvikotlin/extensions/androidx/statekeeper/SerializableStateKeeperProvider.kt) - preserves `Serializable` data.

## Retaining objects

Another use case is to retain an object instance over its scope recreation. This is also commonly used in Android when configuration changes occur. MVIKotlin `keepers` module provides a solution for this as well:

- [InstanceKeeper](https://github.com/arkivanov/MVIKotlin/blob/master/keepers/src/commonMain/kotlin/com/arkivanov/mvikotlin/keepers/instancekeeper/InstanceKeeper.kt) - this interface is used to save and retrieve object instances by key. It has just one method `getOrCreate(key, factory)`, as well as a few handy extension functions. When `getOrCreate` method is called first time, the factory function is called and the returned instance is saved (retained). For any future invocation the retained instance is returned, and the factory function is not invoked.

There is a default implementation available:

- [DefaultInstanceKeeper](https://github.com/arkivanov/MVIKotlin/blob/master/keepers/src/commonMain/kotlin/com/arkivanov/mvikotlin/keepers/instancekeeper/DefaultInstanceKeeper.kt) - it just stores all retained objects in memory.

An extension for AndroidX that can be used in `Fragments` and `Activities`:

- [getInstanceKeeper()](https://github.com/arkivanov/MVIKotlin/blob/master/keepers/src/androidMain/kotlin/com/arkivanov/mvikotlin/keepers/instancekeeper/AndroidInstanceKeeper.kt) - retains instances over Android configuration changes.

### Examples

#### Preserving state of a Store

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    fun create(stateKeeper: StateKeeper<State>): CalculatorStore =
        object : CalculatorStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "CounterStore",
            initialState = stateKeeper.getState() ?: State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {
        }.also {
            stateKeeper.register { 
                it.state.copy(isLoading = false) // We can reset any transient state here
            }
        }

    // Omitted code
}
```

#### Retaining a whole Store

```kotlin
class CalculatorController(instanceKeeper: InstanceKeeper) {

    private val store: CalculatorStore =
        instanceKeeper.getOrCreateStore(::calculatorStore)

    /*
     * Create a new instance of CalculatorStore.
     * ⚠️ Pay attention to not leak any dependencies.
     */
    private fun calculatorStore(): CalculatorStore = // Create the Store
}

```

#### Retaining an arbitrary object

```kotlin
class CalculatorController(instanceKeeper: InstanceKeeper) {

    private val something: Something =
        instanceKeeper.getOrCreate(::Something)

    /*
     * Instances of this class will be retained.
     * ⚠️ Pay attention to not leak any dependencies.
     */
    private class Something : InstanceKeeper.Instance {
        override fun onDestroy() {
            // Clean-up any resources here
        }
    }
}
```

#### Preserving instance state in Android

```kotlin
class MainActivity : AppCompatActivity() { // Same for AndroidX Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stateKeeperProvider = getSerializableStateKeeperProvider()
        // Pass the StateKeeperProvider to dependencies
    }
}
```

#### Retaining objects over Android configuration change

```kotlin
class MainActivity : AppCompatActivity() { // Same for AndroidX Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val instanceKeeper = getInstanceKeeper()
        // Pass the InstanceKeeper to dependencies
    }
}
```

[Overview](index.md) | Store | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | State preservation | [Logging](logging.md) | [Time travel](time_travel.md)
