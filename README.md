## Setup:

### Gradle:

In your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
In your module's build.gradle:
```
dependencies {
    implementation 'com.github.arkivanov:mvidroid:1.0.8'
}
```
### Maven:

Respository:
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Dependency:
```
<dependency>
    <groupId>com.github.arkivanov</groupId>
    <artifactId>mvidroid</artifactId>
    <version>1.0.8</version>
</dependency>
```

## What is MVI

MVI (Model-View-Intent) is a design pattern where Model is an active component which accepts Intents from View and produces new view models (changed state) back to View.

![Model-View-Intent](https://s8.postimg.cc/8xqom7e0l/MVI2.png)
<br>

## What is MVIDroid

MVIDroid is a framework written 100% in Kotlin which brings MVI pattern to Android. It is specially designed and optimized for Android platform, very lightweight and efficient.

## Base concepts

### Store

In MVIDroid Model is represented by MviStore interface (Store). This is the main component of MVIDroid, the place where all magic happens. Store accepts Intents as input (invoke() method) and produces States (states field) and Labels (labels field) as output, and holds current State (state field) so you can access it at any time. Also MviStore implements Disposable interface which makes possible the disposal of Store and cancellation of all running asynchronous operations.

![Store](https://s8.postimg.cc/3mbs1hznp/Store2.png)

Here is how MviStore interface looks like:

```kotlin
interface MviStore<State : Any, in Intent : Any, Label : Any> : (Intent) -> Unit, Disposable {

    @get:MainThread
    val state: State

    val states: Observable<State>
    val labels: Observable<Label>

    @MainThread
    override fun invoke(intent: Intent)

    @MainThread
    override fun dispose()

    @MainThread
    override fun isDisposed(): Boolean
}
```

To send an Intent you simply call "invoke()" method. Use field "state" to get current State of Store and field "states" to subscribe for State updates. Use field "labels" to subscribe for Labels.

### State

State is a data produced by Store, typically represented as a data class (Kotlin). It is a good practice to keep Store’s States immutable.

### Intent

Intent is a call to action accepted by Store. Each Intent triggers some specific job (Action) in Store which produces new States and/or Labels.

### Label

Labels are special events produced by Store used for inter-Store communication. When Store produces a Label it then gets converted to another Stores’ Intents. So one Label mapped to one or more Intents.

### View

In MVIDroid View is represented by MviView interface and its abstract implementation MviAbstractView which you should extend in order to implement a view. It accepts View Models (subscribe() method) and produces UI Events (uiEvents field).

![View](https://s8.postimg.cc/weijkrp91/View2_1.png)

MviView interface:

```kotlin
interface MviView<ViewModel : Any, UiEvent : Any> {

    val uiEvents: Observable<UiEvent>

    @MainThread
    fun subscribe(models: Observable<ViewModel>): Disposable
}
```

### Component

Component is represented by MviComponent interface and its abstract implementation MviAbstractComponent. There are number of purposes of Component:
* to provide States of Stores (states field)
* to accept UI Events (invoke() method) and Labels from outside, transform them to Intents and pass to appropriate Stores
* to pass Labels from Stores to outside
* to dispose Stores when Component is disposed itself (Component implements Disposable interface)
* to act as a facade of Stores, making Component completely independent from UI

![Component](https://s8.postimg.cc/dap84czj9/Component.png)

MviComponent interface:

```kotlin
interface MviComponent<in UiEvent : Any, out States : Any> : (UiEvent) -> Unit, Disposable {

    @get:MainThread
    val states: States

    @MainThread
    override fun invoke(event: UiEvent)

    @MainThread
    override fun dispose()

    @MainThread
    override fun isDisposed(): Boolean
}
```

## Implementing a Store

Lets start from defining our goal: we need to load some text string from server.

To load the string we need some sort of data source, so lets create a simple interface for data source. We won’t implement it as it’s out of topic.

```kotlin
interface NetworkDataSource {

    fun load(): Single<String>
}
```

Now lets define our Store’s interface:

```kotlin
interface LoadDataStore : MviStore<State, Intent, Nothing> {

    data class State(
        val isLoading: Boolean = false,
        val data: String? = null,
        val status: String? = null
    )

    sealed class Intent {
        object LoadData : Intent()
    }
}
```

As your can see our Store’s interface extends MviStore interface and defines three generic parameters: type of State, type of Intents and type of Labels.

Our State is a data class with just two fields:
* "isLoading" flag that will indicate whether there is a loading in progress or not
* "data" field that will contain loaded string
It is recommended to make State completely immutable, which means that State is immutable by itself and all its data is immutable as well. However in some circumstances it might me useful to have some fields mutable.

And there is just one Intent – LoadData. When Store will receive this Intent it will first set "isLoading" flag to true, then load a string and then clear "isLoading" flag and put loaded string into State.

Defining interfaces for Stores is a very good practice, by doing this you are defining a clear public contract which is easy to read and test.

It’s time to implement our LoadDataStore. To create a Store we need to use  MviStoreFactory interface and its implementation MviDefaultStoreFactory. Instead of direct implementation of interface we will create a factory for our Store, which will use provided MviStoreFactory to create an implementation of LoadDataStore.

```kotlin
class LoadDataStoreFactory(
    private val factory: MviStoreFactory,
    private val dataSource: NetworkDataSource
) {
}
```

This is the first step: we have created a class for our factory named LoadDataStoreFactory which accepts MviStoreFactory and NetworkDataSource. Now lets add some stuff.

Actions:

```kotlin
class LoadDataStoreFactory(
    ...
) {

    private sealed class Action {
        object LoadData : Action()
    }
}
```

There is only one Action – LoadData. As name suggests it will trigger the process of loading data.

Results:

```kotlin
class LoadDataStoreFactory(
    ...
) {

    private sealed class Action {
        ...
    }

    private sealed class Result {
        object LoadingStarted : Result()
        class LoadingFinished(val data: String) : Result()
    }
}
```

There are just two results: LoadingStarted will be dispatched before loading and LoadingFinished after that.

Executor:

Executors are used to do all the job. To implement an executor you need to extend abstract class named MviExecutor. It provides you access to a current State of Store via protected field "state", plus it contains two protected methods "dispatch" and "publish" to dispatch Results and publish Labels respectively.

```kotlin
class LoadDataStoreFactory(
    ...
) {

    private sealed class Action {
        ...
    }

    private sealed class Result {
        ...
    }

    private inner class Executor : MviExecutor<State, Action, Result, Nothing>() {
        override fun invoke(action: Action): Disposable? =
            when (action) {
                Action.LoadData -> loadData()
            }

        private fun loadData(): Disposable {
            dispatch(Result.LoadingStarted)

            return dataSource
                .load()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { data -> dispatch(Result.LoadingFinished(data)) }
        }
    }
}
```

In our implementation it receives LoadData Action, immediately produces LoadingStarted Result, then loads data and finally produces LoadingFinished Result.
Please note that Executor along with all other components run on Main thread, and should dispatch Results and produce Labels only on Main thread as well. Current state can also be accessed only from Main thread. 
Please pay special attention that Executor can never be a singleton since it gets connected to Store.

Now we can implement our Reducer:

```kotlin
class LoadDataStoreFactory(
    ...
) {

    private sealed class Action {
        ...
    }

    private sealed class Result {
        ...
    }

    private inner class Executor : MviExecutor<State, Action, Result, Nothing>() {
        ...
    }

    private object Reducer : MviReducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.LoadingStarted -> copy(isLoading = true)
                is Result.LoadingFinished -> copy(isLoading = false, data = result.data)
            }
    }
}
```

Here we simply producing new States based on received Results.

Finally we can create our factory method:

```kotlin
class LoadDataStoreFactory(
    ...
) {

    fun get(): LoadDataStore =
        object : LoadDataStore, MviStore<State, Intent, Nothing> by factory.create(
            initialState = State(),
            intentToAction = {
                when (it) {
                    Intent.LoadData -> Action.LoadData
                }
            },
            executor = Executor(),
            reducer = Reducer
        ) {
        }

    private sealed class Action {
        ...
    }

    private sealed class Result {
        ...
    }

    private inner class Executor : MviExecutor<State, Action, Result, Nothing>() {
        ...
    }

    private object Reducer : MviReducer<State, Result> {
        ...
    }
}
```

Here is the complete code of LoadDataStoreFactory:

```kotlin
class LoadDataStoreFactory(
    private val factory: MviStoreFactory,
    private val dataSource: NetworkDataSource
) {

    fun get(): LoadDataStore =
        object : LoadDataStore, MviStore<State, Intent, Nothing> by factory.create(
            initialState = State(),
            intentToAction = {
                when (it) {
                    Intent.LoadData -> Action.LoadData
                }
            },
            executor = Executor(),
            reducer = Reducer
        ) {
        }

    private sealed class Action {
        object LoadData : Action()
    }

    private sealed class Result {
        object LoadingStarted : Result()
        class LoadingFinished(val data: String) : Result()
    }

    private inner class Executor : MviExecutor<State, Action, Result, Nothing>() {
        override fun invoke(action: Action): Disposable? =
            when (action) {
                Action.LoadData -> loadData()
            }

        private fun loadData(): Disposable {
            dispatch(Result.LoadingStarted)

            return dataSource
                .load()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { data -> dispatch(Result.LoadingFinished(data)) }
        }
    }

    private object Reducer : MviReducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.LoadingStarted -> copy(isLoading = true)
                is Result.LoadingFinished -> copy(isLoading = false, data = result.data)
            }
    }
}
```

## Implementing a Component

Components allows you to group Stores into a single entity which makes it independent from UI. Organizing Stores this way you are free to change UI at any time and as an additional benefit you can share your Components. Clients will have a single entry point instead of dealing with a set of Stores.

To implement a Component we will first define an interface for it:

```kotlin
interface DataComponent : MviComponent<UiEvent, States> {

    sealed class UiEvent {
        object OnLoadData : UiEvent()
    }

    class States(
        val loadDataStates: Observable<LoadDataStore.State>
    )
}
```

Each component defines its UI Events and States. In our case there is only one UI Event (OnLoadData) and only one source of states (loadDataStates). Sometimes it might be useful to put UI Events and/or States in separate files.

Now lets implement our Component:

```kotlin
class DataComponentImpl(
    loadDataStore: LoadDataStore
) : MviAbstractComponent<UiEvent, States>(
    stores = listOf(
        MviStoreBundle(
            store = loadDataStore,
            uiEventTransformer = LoadDataStoreUiEventTransformer
        )
    )
), DataComponent {

    override val states: States =
        States(
            loadDataStates = loadDataStore.states
        )

    private object LoadDataStoreUiEventTransformer : (UiEvent) -> LoadDataStore.Intent? {
        override fun invoke(event: UiEvent): LoadDataStore.Intent? =
            when (event) {
                UiEvent.OnLoadData -> LoadDataStore.Intent.LoadData
            }
    }
}
```

Everything is pretty simple. Please note how we have defined UI Event transformer for our Store. It will transform UI Events into Store’s Intents.

## Implementing a View

It would be good to start from defining our View Model:

```kotlin
data class DataViewModel(
    val isProgressVisible: Boolean,
    val text: String?
)
```

There are again just two fields:
* isProgressVisible – indicates whether we should display progress bar or not
* text – a text to display

Now lets start implementing our View. To do this we need to extend MviAbstractView class. We will implement our View as inner class inside Activity.

```kotlin
class DataActivity : AppCompatActivity() {

    private inner class ViewImpl : MviAbstractView<DataViewModel, DataComponent.UiEvent>() {
        private val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        private val textView = findViewById<TextView>(R.id.text)

        init {
            dispatch(DataComponent.UiEvent.OnLoadData)
        }

        override fun subscribe(models: Observable<DataViewModel>): Disposable =
            CompositeDisposable(
                models.map(DataViewModel::isProgressVisible).distinctUntilChanged().subscribe {
                    progressBar.visibility = if (it) View.VISIBLE else View.GONE
                },

                models.map(DataViewModel::text).distinctUntilChanged().subscribe {
                    textView.text = it
                }
            )
    }
}
```

As always it’s very simple: we are dispatching OnLoadData UI Event and subscribing to View Model updates. We are mapping our View Model field by field using distinctUntilChanged() as we want to set new values into views only if they have changed.

There are a few steps left: we need to map our States to View Model and bind our Component to View.

Here is how our View Model Mapper will look like:

```kotlin
object DataViewModelMapper : MviViewModelMapper<DataComponent.States, DataViewModel> {

    override fun map(states: DataComponent.States): Observable<DataViewModel> =
        states.loadDataStates.map {
            DataViewModel(
                isProgressVisible = it.isLoading,
                text = it.data
            )
        }
}
```

And here is the binding:

```kotlin
class DataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bind(
            DataComponentImpl(
                LoadDataStoreFactory(MviDefaultStoreFactory, createLoadStringDataSource()).get()
            ),
            ViewImpl() using DataViewModelMapper
        )
    }

    private fun createLoadStringDataSource(): NetworkDataSource = TODO()

    private inner class ViewImpl : MviAbstractView<DataViewModel, DataComponent.UiEvent>() {
        ...
    }
}
```

As mentioned above, we won’t implement any data sources in this example, so we just using Kotlin’s TODO().

That’s it!

## Bootstrapper

What if we need to listen for some event from server? Let’s say we need to reload data when received a notification from server. 

We will modify our data source so we can listen for the events:

```kotlin
interface NetworkDataSource {

    val reloadEvents: Observable<Unit>

    ...
}
```

And now it’s time to use Bootstrapper in our Store:

```kotlin
class LoadDataStoreFactory(
    private val factory: MviStoreFactory,
    private val dataSource: NetworkDataSource
) {

    fun get(): LoadDataStore =
        object : LoadDataStore, MviStore<State, Intent, Label> by factory.create(
            ...
            bootstrapper = Bootstrapper(),
            ...
        ) {
        }

    private sealed class Action {
        ...
    }

    private sealed class Result {
        ...
    }

    private inner class Bootstrapper : MviBootstrapper<Action> {
        override fun bootstrap(dispatch: (Action) -> Unit): Disposable? =
            dataSource.reloadEvents.subscribe { dispatch(Action.LoadData) }
    }

    private inner class Executor : MviExecutor<State, Action, Result, Label>() {
        ...
    }

    private object Reducer : MviReducer<State, Result> {
        ...
    }
}
```

Bootstrapper allows us to initialize our Store, in this example we are subscribing to our data source and dispatching same LoadData Action on every event.

## Labels

What if for some reason we need to create one more Store and send events from one to another? Lets say we want to create a separate Store for analytics.

Here is how its interface might look like:

```kotlin
interface AnalyticsStore : MviStore<Unit, Intent, Nothing> {

    sealed class Intent {
        class TrackDataLoaded(val data: String) : Intent()
    }
}
```

And here is an implementation:

```kotlin
class AnalyticsStoreFactory(
    private val factory: MviStoreFactory
) {

    fun get(): AnalyticsStore =
        object : AnalyticsStore, MviStore<Unit, Intent, Nothing> by factory.createActionless(
            initialState = Unit,
            executor = Executor()
        ) {
        }

    private inner class Executor : MviExecutor<Unit, Intent, Nothing, Nothing>() {
        override fun invoke(action: Intent): Disposable? =
            when (action) {
                is Intent.TrackDataLoaded -> TODO()
            }
    }
}
```

Please note that we have used special method from MviStoreFactory that creates a Store without Actions (Intents are passed directly to Executor). There are more special factory methods available.

Now lets modify our LoadDataStore so it will produce the Label every time data is loaded.

Interface:

```kotlin
interface LoadDataStore : MviStore<State, Intent, Label> {

    data class State(
        ...
    )

    sealed class Intent {
        ...
    }

    sealed class Label {
        class DataLoaded(val data: String) : Label()
    }
}
```

Implementation:

```kotlin
class LoadDataStoreFactory(
    ...
) {

    fun get(): LoadDataStore =
        object : LoadDataStore, MviStore<State, Intent, Label> by factory.create(
            ...
        ) {
        }

    private sealed class Action {
        ...
    }

    private sealed class Result {
        ...
    }

    private inner class Bootstrapper : MviBootstrapper<Action> {
        ...
    }

    private inner class Executor : MviExecutor<State, Action, Result, Label>() {
        ...

        private fun loadData(): Disposable {
            .

            return dataSource
                ...
                .subscribe { data ->
                    ...
                    publish(Label.DataLoaded(data))
                }
        }
    }

    private object Reducer : MviReducer<State, Result> {
        ...
    }
}
```

And finally lets add the new Store to our Component and map LoadDataStore’s Label to AnalyticsStore’s Intent:

```kotlin
class DataComponentImpl(
    ...
    analyticsStore: AnalyticsStore
) : MviAbstractComponent<UiEvent, States>(
    stores = listOf(
        ...
        MviStoreBundle(
            store = analyticsStore,
            labelTransformer = AnalyticsStoreLabelTransformer
        )
    )
), DataComponent {

    override val states: States =
        ...

    private object LoadDataStoreUiEventTransformer : (UiEvent) -> LoadDataStore.Intent? {
        ...
    }

    private object AnalyticsStoreLabelTransformer : (Any) -> AnalyticsStore.Intent? {
        override fun invoke(label: Any): AnalyticsStore.Intent? =
            when (label) {
                is LoadDataStore.Label.DataLoaded -> AnalyticsStore.Intent.TrackDataLoaded(label.data)
                else -> null
            }
    }
}
```

That’s it!
