[Overview](index.md) | [Store](store.md) | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | [State preservation](state_preservation.md) | [Logging](logging.md) | Time travel

## Time travel

Time travel is a power debugging tool. Basically it allows you to record all events and states from all active `Stores`. When events are recorded you can explore them, replay and debug. The core functionality is multiplatform and is available for all supported targets.

If you want time travel you have to use the [TimeTravelStoreFactory](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-timetravel/src/commonMain/kotlin/com/arkivanov/mvikotlin/timetravel/store/TimeTravelStoreFactory.kt).

### Using TimeTravelStoreFactory

The `TimeTravelStoreFactory` is used to create implementations of `Store` that are able to record and replay events. 

Suppose we have the following `Store` factory:

```kotlin
internal class CalculatorStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): CalculatorStore =
        object : CalculatorStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "CounterStore",
            // ...
        ) {
        }

    // ...
}
```

It accepts a `StoreFactory` and uses it to create an implementation of the `CalculatorStore`. You can now pass any `StoreFactory` here. So if you want to add time travel just pass an instance of the `TimeTravelStoreFactory`:

```kotlin
val storeFactory = TimeTravelStoreFactory(DefaultStoreFactory)

CalculatorStoreFactory(storeFactory).create()
```

You can also combine it with the `LoggingStoreFactory` in the following way:

```kotlin
val storeFactory = LoggingStoreFactory(TimeTravelStoreFactory(DefaultStoreFactory))

CalculatorStoreFactory(storeFactory).create()
```

Normally you should define a global `StoreFactory` somewhere in the main app and pass it down to all the dependencies.

### Time travel UI

You can integrate a special UI into your app which provides controls for time travel and displays a list of recorded events. It is also possible to explore and debug any recorded event. Debugging means you can put a breakpoint in your code and fire a previously recorded event. You can do usual debugging when the breakpoint is triggered.

For Android you can use the [TimeTravelView](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-timetravel/src/androidMain/kotlin/com/arkivanov/mvikotlin/timetravel/widget/TimeTravelView.kt).
For iOS you can copy-paste [TimeTravelViewController](https://github.com/arkivanov/MVIKotlin/blob/master/sample/todo-app-ios/todo-app-ios/TimeTravelViewController.swift) from the sample app.

Check out the following videos demonstrating time travel UI: 
- [Debugging Android application with MVIKotlin](https://youtu.be/_bbxR503-u0)
- [Debugging iOS application with MVIKotlin](https://youtu.be/MJCYQzeL-w8)

Alternatively you can create your own time travel UI. Please refer to [TimeTravelController](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-timetravel/src/commonMain/kotlin/com/arkivanov/mvikotlin/timetravel/controller/TimeTravelController.kt) for more information.

### Time travel plugin for IntelliJ IDEA and Android Studio

There is a more convenient tool for Android - time travel IDEA plugin. This can be used directly from IDE so there is no need to integrate any additional UI.

You will need to run the [TimeTravelServer](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-timetravel/src/androidMain/kotlin/com/arkivanov/mvikotlin/timetravel/server/TimeTravelServer.kt) in your Android app so the plugin could connect to it. Please refer to the [samples](https://github.com/arkivanov/MVIKotlin/tree/master/sample/todo-app-android) for more examples.

Check out the video demonstrating the time travel IDEA plugin:

[![Debugging Android application with IntelliJ IDEA time travel plugin](https://img.youtube.com/vi/Tr2ayOcVU34/0.jpg)](https://youtu.be/Tr2ayOcVU34)

Check out the video demonstrating how you can export/import the time travel data using the IDEA plugin:

[![Export/import time travel data in Android with MVIKotlin IDEA plugin](https://img.youtube.com/vi/SIxfSgBkHS0/0.jpg)](https://youtu.be/SIxfSgBkHS0)

#### How to install

You can find the plugin in the IntelliJ IDEA [Marketplace](https://plugins.jetbrains.com/plugin/14241-mvikotlin-time-travel). It can be installed directly from IntelliJ IDEA or Android Studio. Please navigate to Settings -> Plugins -> Marketplace and type "MVIKotlin" in the search field.

### Time travel client app for macOS

This is a standalone time travel client for macOS that can connect to iOS and macOS apps.

Same as with the IDEA plugin you will need to run the [TimeTravelServer](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-timetravel/src/darwinCommonMain/kotlin/com/arkivanov/mvikotlin/timetravel/server/TimeTravelServer.kt) in your iOS or macOS app, so the client app could connect to it. Please refer to the [samples](https://github.com/arkivanov/MVIKotlin/tree/master/sample/todo-app-ios) for more examples.

Check out the video demonstrating the time travel client app for macOS:

[![Debugging iOS application using MVIKotlin time travel client app](https://img.youtube.com/vi/rj6GwA2ZQkk/0.jpg)](https://youtu.be/rj6GwA2ZQkk)

#### How to install

The time travel client application for macOS is not published yet so you will need to build it from [sources](https://github.com/arkivanov/MVIKotlin/blob/master/mvikotlin-timetravel-client/app-macos).

[Overview](index.md) | [Store](store.md) | [View](view.md) | [Binding and Lifecycle](binding_and_lifecycle.md) | [State preservation](state_preservation.md) | [Logging](logging.md) | Time travel
