[Overview](index.md) | [Store](store.md) | View | [Binding and Lifecycle](binding_and_lifecycle.md) | [State preservation](state_preservation.md) | [Logging](logging.md) | [Time travel](time_travel.md)

## View

It is not necessary to follow any particular guide when implementing `Views`, however you may find useful what is provided by MVIKotlin.

In MVIKotlin there are two basic interfaces related to `View`:
- [ViewRenderer](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/view/ViewRenderer.kt) - consumes and renders `Models`
- [ViewEvents](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/view/ViewEvents.kt) - produces `Events`

There is also the [MviView](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/view/MviView.kt) interface which is just a combination of both `ViewRenderer` and `ViewEvents` interfaces. Again you normally don't need to implement the `MviView` interface directly. Instead you can extend the [BaseMviView](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/view/BaseMviView.kt) class.

> ⚠️ If you are using Jetpack Compose then most likely you don't need `MviView` or any of its super types. You can observe the `Store` directly in `@Composable` functions. See [Compose TodoApp example](https://github.com/JetBrains/compose-jb/tree/master/examples/todoapp) for more information.

## Implementing a View

Let's implement a `View` for the `CalculatorStore` created [here](store.md). As always we should first define an interface:

```kotlin
interface CalculatorView : MviView<Model, Event> {

    data class Model(
        val value: String
    )

    sealed class Event {
        object IncrementClicked: Event()
        object DecrementClicked: Event()
    }
}
```

The `CalculatorView` is public so it can be implemented natively by every platform, e.g. Android and iOS. Our `CalculatorView` consumes a simple `Model` with just a value text and produces two `Events` (`IncrementClicked` and `DecrementClicked`).

You may notice that `Model` and `Events` look very similar to `CalculatorStore.State` and `CalculatorStore.Intent`. In this particular case our `CalculatorView` could directly render `State` and produce `Intents`. But in general it is a good practice to have separate `Models` and `Events`. This removes coupling between `Views` and `Stores`.

An Android implementation can look like this:

```kotlin
class CalculatorViewImpl(root: View) : BaseMviView<Model, Event>(), CalculatorView {

    private val textView = root.requireViewById<TextView>(R.id.text)

    init {
        root.requireViewById<View>(R.id.button_increment).setOnClickListener {
            dispatch(Event.IncrementClicked)
        }
        root.requireViewById<View>(R.id.button_decrement).setOnClickListener {
            dispatch(Event.DecrementClicked)
        }
    }

    override fun render(model: Model) {
        super.render(model)

        textView.text = model.value
    }
}
```

Here is a possible iOS implementation using SwiftUI:

```swift
class CalculatorViewProxy: BaseMviView<CalculatorViewModel, CalculatorViewEvent>, CalculatorView, ObservableObject {

    @Published var model: CalculatorViewModel?

    override func render(model: CalculatorViewModel) {
        self.model = model
    }
}

struct CalculatorView: View {
    @ObservedObject var proxy = CalculatorViewProxy()

    var body: some View {
        VStack {
            Text(proxy.model?.value ?? "")

            Button(action: { self.proxy.dispatch(event: CalculatorViewEvent.IncrementClicked()) }) {
                Text("Increment")
            }

            Button(action: { self.proxy.dispatch(event: CalculatorViewEvent.DecrementClicked()) }) {
                Text("Decrement")
            }
        }
    }
}

```

For a more complex UI please refer to the [samples](https://github.com/arkivanov/MVIKotlin/tree/master/sample).

### Efficient view updates

Sometimes it may be inefficient to update the entire `View` each time a new `Model` is received. For example, if a `View` contains a text and a list, it may be useful not to update the list if only the text is changed. MVIKotlin provides the [diff](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin/src/commonMain/kotlin/com/arkivanov/mvikotlin/core/utils/Diff.kt) tool for this.

Suppose we have a `UserInfoView` that displays a user's name and a list of friends:

```kotlin
interface UserInfoView : MviView<Model, Nothing> {

    data class Model(
        val name: String,
        val friendNames: List<String>
    )
}
```

We can use `diff` in the following way:

```kotlin
class UserInfoViewImpl : BaseMviView<Model, Nothing>(), UserInfoView {

    private val nameText: TextView = TODO()
    private val friendsList: ListView = TODO()

    override val renderer: ViewRenderer<Model>? = diff {
        diff(get = Model::name, set = nameText::setText)
        diff(get = Model::friendNames, compare = { a, b -> a === b }, set = friendsList::setItems)
    }
}
```

Every `diff` statement accepts a `getter` that extracts a value from the `Model`, a `setter` that sets the value to the view and an optional `comparator` of values.

[Overview](index.md) | [Store](store.md) | View | [Binding and Lifecycle](binding_and_lifecycle.md) | [State preservation](state_preservation.md) | [Logging](logging.md) | [Time travel](time_travel.md)
