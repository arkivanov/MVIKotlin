## Time travel client app for macOS

### How to build

Just open the Xcode project and select `Product -> Build`.
The first build will take a while because it will build the `TimeTravelClient` framework first (`client-internal` module).

### Debugging in Xcode

You can export Kotlin sources to Xcode via [KotlinXcodeSync](https://github.com/touchlab/KotlinXcodeSync) plugin. If you want to use Xcode debugger with Kotlin you can use [xcode-kotlin](https://github.com/touchlab/xcode-kotlin) plugin for Xcode.

### Sample app

You can try time travel debugging with the [sample todo app for iOS](https://github.com/arkivanov/MVIKotlin/tree/master/sample/todo-app-ios).

### Watch video

Please watch the following video demonstrating time travel debugging:

[![Debugging iOS application using MVIKotlin Time Travel Client app](https://img.youtube.com/vi/rj6GwA2ZQkk/0.jpg)](https://youtu.be/rj6GwA2ZQkk)
