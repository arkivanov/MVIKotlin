[Overview](index.md) | [Store](store.md) | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | State preservation | [Logging](logging.md) | [Time travel](time_travel.md)

## State preservation

Sometimes it might be necessary to preserve a state (e.g. a state of a `Store`) in order to restore it later. A very common use case is Android Activity recreation due to configuration changes, or process death. If you are working on a pure Android project (not multiplatform) then AndroidX [SavedStateRegistry](https://developer.android.com/reference/androidx/savedstate/SavedStateRegistry) can be used directly. For multiplatform projects you can use the `StateKeeper` from [Essenty](https://github.com/arkivanov/Essenty) library (from the same author). Please familiarise yourself with Essenty library, especially with the `StateKeeper`.

## Retaining objects

Another use case is to retain an object instance over its scope recreation. This is also commonly used in Android when configuration changes occur. If you are working on a pure Android project (not multiplatform) then AndroidX [ViewModelStore](https://developer.android.com/reference/android/arch/lifecycle/ViewModelStore) and [ViewModelProvider](https://developer.android.com/reference/android/arch/lifecycle/ViewModelProvider) can be used directly. For multiplatform projects you can use `InstanceKeeper` from [Essenty](https://github.com/arkivanov/Essenty) library (from the same author). Please familiarise yourself with Essenty library, especially with the `InstanceKeeper`.

### Examples

#### Preserving state of a Store

```kotlin
internal interface CalculatorStore : Store<Intent, State, Nothing> {
    @Parcelize
    data class State(
        val isLoading: Boolean = false,
        // Other properties
    ) : Parcelable

    // Omitted code
}

internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    fun create(stateKeeper: StateKeeper): CalculatorStore =
        object : CalculatorStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "CounterStore",
            initialState = stateKeeper.consume(key = "CalculatorStoreState") ?: State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {
        }.also {
            stateKeeper.register(key = "CalculatorStoreState") {
                it.state.copy(isLoading = false) // We can reset any transient state here
            }
        }

    // Omitted code
}
```

#### Retaining a whole Store

```kotlin
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.instancekeeper.getStore

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
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate

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

#### Creating StateKeeper in Android

```kotlin
import com.arkivanov.essenty.statekeeper.stateKeeper

class MainActivity : AppCompatActivity() { // Same for AndroidX Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stateKeeper = stateKeeper()
        // Pass the StateKeeper to dependencies
    }
}
```

#### Creating InstanceKeeper in Android
```kotlin
import com.arkivanov.essenty.instancekeeper.instanceKeeper

class MainActivity : AppCompatActivity() { // Same for AndroidX Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val instanceKeeper = instanceKeeper()
        // Pass the InstanceKeeper to dependencies
    }
}
```

[Overview](index.md) | Store | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | State preservation | [Logging](logging.md) | [Time travel](time_travel.md)
