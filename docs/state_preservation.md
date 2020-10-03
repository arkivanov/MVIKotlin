[Overview](index.md) | [Store](store.md) | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | State preservation | [Logging](logging.md) | [Time travel](time_travel.md)

## State preservation

Sometimes it might be necessary to preserve a state (e.g. a state of a `Store`) in order to restore it later. A very common use case is Android Activity recreation due to system constraints. If you are working on a pure Android project (not multiplatform) then AndroidX [SavedStateRegistry](https://developer.android.com/reference/androidx/savedstate/SavedStateRegistry) can be used directly. For multiplatform projects MVIKotlin provides an abstraction - the `StateKeeper`.

The `StateKeeper` is an interface for state (or any other data) preservation. In general it does not make any assumptions on what is being preserved and how. The `StateKeeper` is provided by the `keepers` module.

There are several interfaces related to this.

- [StateKeeper](https://github.com/arkivanov/MVIKotlin/blob/master/keepers/src/commonMain/kotlin/com/arkivanov/mvikotlin/keepers/statekeeper/StateKeeper.kt) - this generic interface is used to retrieve saved data, if any, and to register a data supplier. The data supplier is called when it's time to save the state. The generic type parameter ensures that types of the saved and restored data are same.
- [StateKeeperRegistry](https://github.com/arkivanov/MVIKotlin/blob/master/keepers/src/commonMain/kotlin/com/arkivanov/mvikotlin/keepers/statekeeper/StateKeeperRegistry.kt) - this generic interface provides and manages typed instances of the `StateKeeper`.

There are extensions for AndroidX available, they can be used in `Fragments` and `Activities`:
- [getParcelableStateKeeperRegistry()](https://github.com/arkivanov/MVIKotlin/blob/master/keepers/src/androidMain/kotlin/com/arkivanov/mvikotlin/keepers/statekeeper/ParcelableStateKeeperRegistry.kt) - preserves `Parcelable` data.
- [getSerializableStateKeeperRegistry()](https://github.com/arkivanov/MVIKotlin/blob/master/keepers/src/androidMain/kotlin/com/arkivanov/mvikotlin/keepers/statekeeper/SerializableStateKeeperRegistry.kt) - preserves `Serializable` data.

## Retaining objects

Another use case is to retain an object instance over its scope recreation. This is also commonly used in Android when configuration changes occur. If you are working on a pure Android project (not multiplatform) then AndroidX [ViewModelStore](https://developer.android.com/reference/android/arch/lifecycle/ViewModelStore) and [ViewModelProvider](https://developer.android.com/reference/android/arch/lifecycle/ViewModelProvider) can be used directly. For multiplatform projects MVIKotlin provides an abstraction:

- [InstanceKeeper](https://github.com/arkivanov/MVIKotlin/blob/master/keepers/src/commonMain/kotlin/com/arkivanov/mvikotlin/keepers/instancekeeper/InstanceKeeper.kt) - this interface is used to save and retrieve object instances by key, it is provided by the `keepers` module. It has just one method `get(key, factory)`, as well as a few handy extension functions. When `get` method is called first time, the factory function is called and the returned instance is saved (retained). For any future invocation the retained instance is returned, and the factory function is not invoked.

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
            initialState = stateKeeper.consume() ?: State(),
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
        instanceKeeper.getStore(::calculatorStore)

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
        instanceKeeper.get(::Something)

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

        val stateKeeperRegistry = getParcelableStateKeeperRegistry() // Or getSerializableStateKeeperRegistry()
        // Pass the StateKeeperRegistry to dependencies
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
