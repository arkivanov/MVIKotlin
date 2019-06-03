## View

Almost every app needs UI. MVIDroid can help you in this area as well.
There is an interface called `MviView`, here is how it looks like:
```kotlin
interface MviView<in ViewModel : Any, out ViewEvent : Any> {

    val events: Observable<out ViewEvent>

    @MainThread
    fun bind(model: ViewModel)
}
```

As you can see `View` just produces `View Events` and accepts `View Models`.

![View](media/view.jpg)

Let's create a `View` that will display user info. First we need
to define our `View Model`:
```kotlin
data class UserInfoViewModel(
    val name: CharSequence?,
    val about: CharSequence?
)
```

It's very simple, just two fields for `name` and `about` texts.

To implement a `View` you can just implement `MviView` interface, or you
can extend `MviBaseView` class which provides some additional features.

```kotlin
class UserInfoView(root: View) : MviBaseView<UserInfoViewModel, Nothing>() {

    private val nameTextView = root.findViewById<TextView>(R.id.text_name)
    private val aboutTextView = root.findViewById<TextView>(R.id.text_about)

    override fun bind(model: UserInfoViewModel) {
        super.bind(model)

        nameTextView.text = model.name
        aboutTextView.text = model.about
    }
}
```

We have extended `MviBaseView` class and used `bind(ViewModel)` method
to set data to our views.

Now let's add ability to modify user info. First we need `View Events`:
```kotlin
sealed class UserInfoViewEvent {
    object OnNameClick : UserInfoViewEvent()
    object OnAboutClick : UserInfoViewEvent()
    object OnEditDialogShown : UserInfoViewEvent()
    class OnNameChanged(val name: CharSequence) : UserInfoViewEvent()
    class OnAboutChanged(val about: CharSequence) : UserInfoViewEvent()
}
```

We have defined five `View Events`:
* OnNameClick and OnAboutClick will be dispatched when user taps on name
and about text fields respectively
* OnEditDialogShown will be dispatched when view shows a dialog with
edit invitation
* OnNameChanged and OnAboutChanged will be dispatched when user closes
edit dialog using OK button.

And here is our updated `View Model`:
```kotlin
data class UserInfoViewModel(
    val name: CharSequence?,
    val about: CharSequence?,
    val nameEditDialogText: CharSequence?,
    val aboutEditDialogText: CharSequence?
)
```

You can observe two new fields: `nameEditDialogText` and
`aboutEditDialogText`. They are tricky. We can call such fields as
"consumable events". When `View` observes any of such fields in
`View Model`, it first dispatches a special `View Event` indicating that
"event" is consumed. And after that `View` actually handles the "event":
it can show a dialog, a toast or even open another screen.

Let's see how it works:
```kotlin
class UserInfoView(root: View) : MviBaseView<UserInfoViewModel, UserInfoViewEvent>() {

    ...

    init {
        nameTextView.setOnClickListener { dispatch(UserInfoViewEvent.OnNameClick) }
        aboutTextView.setOnClickListener { dispatch(UserInfoViewEvent.OnAboutClick) }
    }

    override fun bind(model: UserInfoViewModel) {
        super.bind(model)

        nameTextView.text = model.name
        aboutTextView.text = model.about

        model.nameEditDialogText?.also { text ->
            dispatch(UserInfoViewEvent.OnEditDialogShown)
            showEditDialog(text) { dispatch(UserInfoViewEvent.OnNameChanged(it)) }
        }

        model.aboutEditDialogText?.also { text ->
            dispatch(UserInfoViewEvent.OnEditDialogShown)
            showEditDialog(text) { dispatch(UserInfoViewEvent.OnAboutChanged(it)) }
        }
    }

    private inline fun showEditDialog(text: CharSequence, crossinline onTextConfirmedListener: (CharSequence) -> Unit) {
        // Show edit dialog here
    }
}
```

Alternatively you can create your own mutable `SingleLifeEvent` that can
be used only once and then cleared. In this case you don't have to
"consume" the events. You can find an example of `SingleLifeEvent` in the
Sample App.

Now we can edit every field of user info. And there is one problem now,
we are rebinding whole view every time, even if there is only one field
changed. Fortunately `MVIDroid` comes with a nice model diff feature.

```kotlin
class UserInfoView(root: View) : MviBaseView<UserInfoViewModel, UserInfoViewEvent>() {

    init {
        root
            .findViewById<TextView>(R.id.text_name)
            .apply {
                setOnClickListener { dispatch(UserInfoViewEvent.OnNameClick) }
            }
            .also { diff.diffByEquals(UserInfoViewModel::name, it::setText) }

        root
            .findViewById<TextView>(R.id.text_about)
            .apply {
                setOnClickListener { dispatch(UserInfoViewEvent.OnAboutClick) }
            }
            .also { diff.diffByEquals(UserInfoViewModel::about, it::setText) }

        diff.diffByEquals(UserInfoViewModel::nameEditDialogText) { text ->
            if (text != null) {
                dispatch(UserInfoViewEvent.OnEditDialogShown)
                showEditDialog(text) { dispatch(UserInfoViewEvent.OnNameChanged(it)) }
            }
        }

        diff.diffByEquals(UserInfoViewModel::aboutEditDialogText) { text ->
            if (text != null) {
                dispatch(UserInfoViewEvent.OnEditDialogShown)
                showEditDialog(text) { dispatch(UserInfoViewEvent.OnAboutChanged(it)) }
            }
        }
    }

    private inline fun showEditDialog(text: CharSequence, crossinline onTextConfirmedListener: (CharSequence) -> Unit) {
        // Show edit dialog here
    }
}
```

`MviBaseView` contains protected 'diff: ModelDiff' that allows you to 
diff `View Models` field by field using custom comparators and assign
values to your views only if they are changed. Plus there are two useful
extensions methods, `diffByEquals(Mapper, Consumer)` and `diffByReference(Mapper, Consumer)`,
that provide diffing by `equals` and by `reference` respectively.

---
[Previous](store.md) [Index](index.md) [Next](binding.md)
