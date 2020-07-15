[Overview](index.md) | Store | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | State preservation | [Logging](logging.md) | [Time travel](time_travel.md)

## State preservation

Sometimes it might be necessary to save the current `State` of a `Store` (or maybe a whole instance of a `Store`) in order to restore it later. A very common use case is configuration change in Android. MVIKotlin provides a utility for state preservation - the `StateKeeper`.

The `StateKeeper` is a utility for state (or any other data) preservation. In general it does not make any assumptions on what is being preserved and how. The `StateKeeper` is provided by the `mvikotlin` module.

There are several interfaces related to this.

- [StateKeeper](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/statekeeper/StateKeeper.kt) - this generic interface is used to retrieve saved data, if any, and to register a data supplier. The data supplier is called when it's time to save the state. The generic type parameter ensures that types of the saved and restored data are same;
- [StateKeeperProvider](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/statekeeper/StateKeeperProvider.kt) - this generic interface provides typed instances of the `StateKeeper`;
- [StateKeeperController](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/statekeeper/StateKeeperController.kt) - this generic interface is used to actually save and restore data. Its generic type parameters ensure that all saved data conform to a base type.

There are several `StateKeeperControllers` provided by MVIKotlin:

- [SimpleStateKeeperController](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/statekeeper/SimpleStateKeeperControllerFactory.kt) - this is for in-memory data preservation. It accepts `Any` data so it can not be serialized or somehow persisted. One possible use case is to preserve data over configuration change in Android. You can find usage example [here](https://github.com/arkivanov/MVIKotlin/blob/master/sample/todo-app-android/src/main/java/com/arkivanov/mvikotlin/sample/todo/android/MainActivity.kt). This controller is multiplatform.
- [SerializableStateKeeperController](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/androidMain/kotlin/com/arkivanov/mvikotlin/core/statekeeper/SerializableStateKeeperControllerFactory.kt) -  this controller is only for Android, it saves `Serializable` data into `Bundle`;
- [ParcelableStateKeeperController](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/androidMain/kotlin/com/arkivanov/mvikotlin/core/statekeeper/ParcelableStateKeeperControllerFactory.kt) - same as previous controller but saves `Parcelable` data into `Bundle`.

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

#### Retaining a whole Store without Lifecycle

```kotlin
class CalculatorController(stateKeeperProvider: StateKeeperProvider<Any>) {

    private val store: CalculatorStore

    init {
        val stateKeeper: StateKeeper<CalculatorStore> = stateKeeperProvider.get()
        store = stateKeeper.getState() ?: calculatorStore()
        stateKeeper.register { store }
    }
    
    private fun calculatorStore(): CalculatorStore = // Create the Store
    
    fun dispose() {
        store.dispose()
    }
}
```

#### Retaining a whole Store with Lifecycle

```kotlin
class CalculatorController(
    stateKeeperProvider: StateKeeperProvider<Any>,
    lifecycle: Lifecycle
) {

    private val store: CalculatorStore = 
        stateKeeperProvider.retainStore(lifecycle) { calculatorStore() }

    private fun calculatorStore(): CalculatorStore = // Create the Store
}
```

#### Retaining an arbitrary object with Lifecycle

```kotlin
class MyFragment(
    private val stateKeeperProvider: StateKeeperProvider<Any>
) : Fragment() {

    private lateinit var controller: CalculatorController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        controller = stateKeeperProvider.retainInstance(lifecycle.asMviLifecycle(), ::calculatorController)
    }

    /*
     * Create the controller.
     * The provided Lifecycle will not be destroyed when the instance is retained.
     */
    private fun calculatorController(lifecycle: Lifecycle): CalculatorController = // Create the controller
}
```

#### Retaining data over Android configuration change

```kotlin
class MainActivity : AppCompatActivity() {

    private val nonConfigurationStateKeeperController = 
        SimpleStateKeeperController { lastCustomNonConfigurationInstance as MutableMap<String, Any>? }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pass the nonConfigurationStateKeeperController as StateKeeperProvider to dependencies
    }

    override fun onRetainCustomNonConfigurationInstance(): Any? =
        nonConfigurationStateKeeperController.saveAndGet(HashMap())
}
```

#### Preserving instance state in Android

```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var savedStateKeeperController: SerializableStateKeeperController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedStateKeeperController = SerializableStateKeeperController { savedInstanceState }

        // Pass savedStateKeeperController as StateKeeperProvider to dependencies
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        savedStateKeeperController.save(outState)
    }
}
```

[Overview](index.md) | Store | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | State preservation | [Logging](logging.md) | [Time travel](time_travel.md)
