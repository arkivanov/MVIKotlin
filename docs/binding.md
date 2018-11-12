## Binding

In previous chapters we learned about three main things of MVIDroid:
`Store`, `Component` and `View`. And now it's time to bind them together.

Let's start from simplest case.

Suppose we have `Store` and its `State`:
```kotlin
data class UserInfoState(
    ...
)

interface UserInfoStore : MviStore<UserInfoState, UserInfoStore.Intent, Nothing> {
    sealed class Intent {
        ...
    }
}

class UserInfoStoreFactory {
    fun create(): UserInfoStore = TODO()
}
```

And we have a `View`:
```kotlin
class UserInfoView : MviBaseView<UserInfoState, UserInfoStore.Intent>() {
    ...
}
```

As you can see in simplest case we can avoid using `View Models` and
`View Events`, our `View` accepts `States` and produces `Intents`.

Our `Activity` can look something like this:
```kotlin
class UserInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binder(UserInfoStoreFactory().create())
            .addView(UserInfoView())
            .bind()
            .attachTo(this)
    }
}
```

We used `MviBinder` to bind `Store` with `View`. Its `bind()` method
returns `MviLifecycleObserver` that has a number of methods to control
life-cycle. And there is an extension method `attachTo(LifecycleOwner)`
that attaches `MviLifecycleObserver` to life-cycle of `Activity`.

What if need to make our `View` independent from business logic
(from `Store`). In this case we will need `View Model` and `View Events`.
```kotlin
data class UserInfoViewModel(
    ...
)

sealed class UserInfoViewEvent {
    ...
}

class UserInfoView : MviBaseView<UserInfoViewModel, UserInfoViewEvent>() {
    ...
}
```

And we need to define two mappers: first one will convert
`State` to `ViewModel` and second one will convert `View Events` to `Intents`.
```kotlin
object UserInfoViewModelMapper : (Observable<out UserInfoState>) -> Observable<out UserInfoViewModel> {
    override fun invoke(states: Observable<out UserInfoState>): Observable<out UserInfoViewModel> {
        ...
    }
}

object UserInfoViewEventMapper : (UserInfoViewEvent) -> UserInfoStore.Intent {
    override fun invoke(event: UserInfoViewEvent): UserInfoStore.Intent {
        ...
    }
}
```

Here is our updated `Activity`:
```kotlin
class UserInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binder(UserInfoStoreFactory().create())
            .addView(UserInfoView(), UserInfoViewModelMapper, UserInfoViewEventMapper)
            .bind()
            .attachTo(this)
    }
}
```

We have passed two mappers to `MviBinder` together with `View`. You
can observe that our `View` is now completely independent from business
logic (from `Store`).

Now what if we have many `Stores` and there is a `Component` around them?
```kotlin
sealed class UserEvent {
    ...
}

data class UserStates(
    ...
)

interface UserComponent : MviComponent<UserEvent, UserStates>

```

Then our mappers will convert `States` to `View Models` and
`View Events` to `Component Events`:
```kotlin
object UserInfoViewModelMapper : (UserStates) -> Observable<out UserInfoViewModel> {
    override fun invoke(states: UserStates): Observable<out UserInfoViewModel> {
        ...
    }
}

object UserInfoViewEventMapper : (UserInfoViewEvent) -> UserEvent {
    override fun invoke(event: UserInfoViewEvent): UserEvent {
        ...
    }
}

```

And we can use same `MviBinder` and bind `Component` to `View`:
```kotlin
class UserInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binder(UserComponentFactory().create())
            ...
    }
}
```

And of course we can add as many `Views` as we want:
```kotlin
class UserInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binder(UserComponentFactory().create())
            .addView(UserInfoView(), UserInfoViewModelMapper, UserInfoViewEventMapper)
            .addView(...)
            .addView(...)
            .addView(...)
            .bind()
            .attachTo(this)
    }
}
```

---
[Previous](view.md) [Index](index.md)
