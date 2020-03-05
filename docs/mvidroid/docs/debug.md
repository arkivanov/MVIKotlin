## Debug

To create a `Store` we use `MviStoreFactory` which accepts parts of
`Store` and creates it. `Store Factory` is a very powerful tool, you can
achieve different behaviours by replacing and/or wrapping them.

The recommended way of implementing a `Store` is using `Factory` pattern:
```kotlin
class DemoStoreFactory(
    private val factory: MviStoreFactory
) {

    fun create(): DemoStore =
        object : MviStore<State, Intent, Nothing> by factory.createExecutorless(
            name = "DemoStore",
            ...
        ), DemoStore {
        }

    ...
}
```

We can pass different implementations of `MviStoreFactory` depending on
circumstances.

### Logging

The simplest debug tool which comes with MVIDroid is logging. Normally
you would pollute your code with a log of "Log.x()" statements, but there
is another way of doing that: we can use `MviLoggingStoreFactory`.

```kotlin
class MviLoggingStoreFactory(
    private val delegate: MviStoreFactory = MviDefaultStoreFactory,
    private val logger: MviLogger = MviDefaultLogger,
    var mode: Mode = Mode.MEDIUM
) {
    ...
}
```

It is actually a wrapper, it accepts another `Store Factory` and logs all
events. Plus you can pass different logger implementations and use
different logging modes.

You can find a working example in sample app.

### Time travel

This is another debug tool. With time travel you can record all events
from all `Stores` in your app. All recorded events appear in event list
where you can:
* view (scroll) entire event list
* switch to any previous state by moving backward and forward in list
(UI reflects the selected state)
* tap on any event to display all its properties (objects are deep-parsed
using Java Reflection and all its properties are displayed in readable
JSON format)
* fire any event again: you can put breakpoint in your code, fire event
(using "Bug" icon) and debug it
* serialize and deserialize events in order to transfer them from one
device to another (remote debug)

Time travel can be enabled using `MviTimeTravelStoreFactory`, just pass
it instead of `MviDefaultStoreFactory` to all your `Store Factories`.
To control time travel and to display its events you can use
`MviTimeTravelView`.

You can find a working example in sample app.

### Watch video

Here is the video showing power of logging and time travel.
[![Debugging with MVIDroid](https://img.youtube.com/vi/_bbxR503-u0/0.jpg)](https://youtu.be/_bbxR503-u0)

[Previous](binding.md) [Index](index.md)
