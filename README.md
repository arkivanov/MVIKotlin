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
    implementation 'com.github.arkivanov.mvidroid:mvidroid:1.2.3'
    implementation 'com.github.arkivanov.mvidroid:mvidroid-debug:1.2.3'
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
    <groupId>com.github.arkivanov.mvidroid</groupId>
    <artifactId>mvidroid</artifactId>
    <version>1.2.3</version>
</dependency>
<dependency>
    <groupId>com.github.arkivanov.mvidroid</groupId>
    <artifactId>mvidroid-debug</artifactId>
    <version>1.2.3/version>
</dependency>
```

## What is MVI

MVI (Model-View-Intent) is a design pattern where `Model` is an active component which accepts `Intents` from `View` and produces new `View Models` (changed state) back to `View`.

![Model-View-Intent](https://s8.postimg.cc/8xqom7e0l/MVI2.png)
<br>

## What is MVIDroid

MVIDroid is a framework written 100% in Kotlin which brings MVI pattern to Android. It is specially designed and optimized for Android platform, very lightweight and efficient. Includes powerful debug tools like logging and time travel. Also MVIDroid provides a pattern for decoupling of business logic and UI, so you can make them completely independent from each other.

## Base concepts

### Store

In MVIDroid `Model` is represented by `MviStore` interface (Store). This is the main component of MVIDroid, the place where all magic happens. `Store` accepts `Intents` as input (`accept()` method) and produces `States` (`states` field) and `Labels` (`labels` field) as output, and holds current `State` (`state` field) so you can access it at any time. Also MviStore implements Disposable interface which makes possible the disposal of `Store` and cancellation of all running asynchronous operations.

![Store](https://s8.postimg.cc/3mbs1hznp/Store2.png)

Here is how `MviStore` interface looks like:

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

To send an `Intent` you simply call `accept(Intent)` method. Use field "state" to get current `State` of `Store` and field "states" to subscribe for `State` updates. Use field `labels` to subscribe for `Labels`.

### State

`State` is a data produced by `Store`, typically represented as a data class (Kotlin). It is a good practice to keep Store’s `States` immutable.

### Intent

`Intent` is a call to action accepted by `Store`. Each `Intent` triggers some specific job (`Action`) in `Store` which produces new `States` and/or `Labels`.

### Label

`Labels` are special events produced by `Store` used for inter-store communication. When `Store` produces a `Label` it then gets converted to another stores’ `Intents`. So one `Label` mapped to one or more `Intents`.

### View

In MVIDroid `View` is represented by `MviView` interface and its base class `MviBaseView` which you should extend in order to implement a view. It accepts `View Models` (`bind(ViewModel)` method) and produces `View Events` (`events` field). `View` also has `onDestroy()` callback which is called automatically when `View` is destroyed.

![View](https://s8.postimg.cc/weijkrp91/View2_1.png)

MviView interface:

```kotlin
interface MviView<in ViewModel : Any, out ViewEvent : Any> {

    val events: Observable<out ViewEvent>

    @MainThread
    fun bind(model: ViewModel)

    @MainThread
    fun onDestroy()
}
```

### Component

`Component` is represented by `MviComponent` interface and its abstract implementation `MviAbstractComponent`. There are number of responsibilities of `Component`:
* provides `States` of `Stores` (`states` field)
* accepts `Events` (`accept()` method) and `Labels`, converts them to `Intents` and passes to appropriate `Stores`
* passes `Labels` from `Stores` to outside
* disposes `Stores` when `Component` is disposed itself (`Component` implements `Disposable` interface)
* acts as a facade of `Stores` which makes `Component` completely independent from UI

![Component](https://s8.postimg.cc/dap84czj9/Component.png)

`MviComponent` interface:

```kotlin
interface MviComponent<in Event : Any, out States : Any> : Disposable {

    @get:MainThread
    val states: States

    @MainThread
    fun accept(event: Event)

    @MainThread
    override fun dispose()

    @MainThread
    override fun isDisposed(): Boolean
}
```

### Binding

Once you have all the components (`Stores`, `Components` and `Views`) you need to bind them together. Use `MviBinder` class and one of provided `binder(...)` methods to create it.

Example of binding views with component:
```kotlin
binder(component)
    .addView(view1)
    .addView(view2, ViewModelMapper)
    .addView(view3, ViewEventMapper)
    .addView(view4, ViewModelMapper, ViewEventMapper)
    .bind()
    .attachTo(Lifecycle)
```

Depending of View and Component you might need to provided `View Model Mapper` and/or `View Event Mapper` which will convert `Component` `States` to `View Models` and/or `View Events` to `Component Events`. If `View` uses same types as `Component` then nothing should be provided. So it is possible to make UI completely independent from business logic.

## Sample project

You can find a sample project in the repo: todo list with details view.
The sample project covers most of the cases:
- Multiple `Stores`
- Multiple screens
- `Components` with multiple `Stores`
- `Stores` shared between `Components`
- Delayed operations and their cancellation
- `Labels`
- View with RecycleView and Adapter
- Logging in debug build
- Time travel drawer in debug build (right side of the screen)
