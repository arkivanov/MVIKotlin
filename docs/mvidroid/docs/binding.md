## Binding

In previous chapters we learned about two main things of MVIDroid:
`Store` and `View`. And now it's time to bind them together.

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

        val store = UserInfoStoreFactory().create().attachTo(lifecycle)
        val view = UserInfoView()

        store
            .states
            .subscribeMvi(view)
            .attachTo(lifecycle)
    }
}
```

We used `Observable.subscribeMvi(...)` extension function to bind `Store`
with `View`. It returns `MviLifecycleObserver` that has a number of
methods to control life-cycle. And there is an extension method
`MviLifecycleObserver.attachTo(Lifecycle)` that attaches
`MviLifecycleObserver` to Android Arch Lifecycle. In this case
`UserInfoView` will be automatically subscribed to `UserInfoStore` on
`Activity` start and unsubscribed on `Activity` stop. Please also note
the handy `Disposable.attachTo(Lifecycle)` extension function, it disposes
the disposable (`UserInfoStore` in our case) at the end of life-cycle.

What if we need to make our `View` independent from business logic
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
object UserInfoViewModelMapper : (UserInfoState) -> UserInfoViewModel {
    override fun invoke(state: UserInfoState): UserInfoViewModel {
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

        val store = UserInfoStoreFactory().create().attachTo(lifecycle)
        val view = UserInfoView()

        store
            .states
            .map(UserInfoViewModelMapper)
            .subscribeMvi(view)
            .attachTo(lifecycle)

        view
            .events
            .map(UserInfoViewEventMapper)
            .subscribe(store)
            .attachTo(lifecycle)
    }
}
```

We bound `UserInfoStore` with `UserInfoView` using mappers. You can observe
that our `View` is now completely independent from business logic (from `Store`).
Please not that we used `Observable.subscribe(...)` function to bind `View Events`
to Intents` as there is not point to unsubscribe/resubscribe them.

---
[Previous](view.md) [Index](index.md) [Next](debug.md)
