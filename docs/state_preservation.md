[Overview](index.md) | Store | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | State preservation | [Logging](logging.md) | [Time travel](time_travel.md)

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

Another use case is to retain an object instance over its scope recreation. This is also commonly used in Android when configuration changes occur. MVIKotlin provides a solution for this as well.

Here are the related interfaces:

- [InstanceKeeper](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/instancekeeper/InstanceKeeper.kt) - this generic interface is used to save and retrieve object instances. It also has an associated [Lifecycle](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/lifecycle/Lifecycle.kt) so one can subscribe to it. The generic type parameter ensures that types of the saved and restored instances are same;

- [InstanceKeeperProvider](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/instancekeeper/InstanceKeeperProvider.kt) - this generic interface provides typed instances of the `InstanceKeeper`;

There is a default implementation available - [InstanceContainer](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/instancekeeper/InstanceContainer.kt). It just stores all retained objects in memory.

An extension for AndroidX is provided by `mvikotlin-extensions-androidx` module, can be used in `Fragments` and `Activities`:
- [getInstanceKeeperProvider()](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-extensions-androidx/src/androidMain/kotlin/com/arkivanov/mvikotlin/extensions/androidx/instancekeeper/AndroidInstanceKeeper.kt) - retains instances over Android configuration changes.

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
class CalculatorController(instanceKeeperProvider: InstanceKeeperProvider) {

    private val store: CalculatorStore =
        instanceKeeperProvider.get<CalculatorStore>().getOrCreateStore(::calculatorStore)

    /*
     * Create a new instance of CalculatorStore.
     * ⚠️ Pay attention to not leak any dependencies.
     */
    private fun calculatorStore(): CalculatorStore = // Create the Store
}

```

#### Retaining an arbitrary object

```kotlin
class CalculatorController(
    instanceKeeperProvider: InstanceKeeperProvider
) : Fragment() {

    private val something: Something =
        instanceKeeperProvider.get<Something>().getOrCreate(::Something)

    /*
     * Create a new instance of Something.
     * ⚠️ Pay attention to not leak any dependencies.
     */
    private class Something(lifecycle: Lifecycle) {
        // ...
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

        val instanceKeeperProvider = getInstanceKeeperProvider()
        // Pass the InstanceKeeperProvider to dependencies
    }
}
```

[Overview](index.md) | Store | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | State preservation | [Logging](logging.md) | [Time travel](time_travel.md)
