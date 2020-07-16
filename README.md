<img src="https://raw.githubusercontent.com/arkivanov/MVIKotlin/master/docs/media/logo/landscape/png/mvikotlin_coloured.png" height="64">

[![Download](https://api.bintray.com/packages/arkivanov/maven/mvikotlin/images/download.svg)](https://bintray.com/arkivanov/maven/mvikotlin/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache/2.0-blue.svg)](https://github.com/arkivanov/MVIKotlin/blob/master/LICENSE)
[![kotlinlang|MVIKotlin](https://img.shields.io/badge/kotlinlang-mvikotlin-blue?logo=slack)](https://kotlinlang.slack.com/archives/C01403U1ZGW)

Should you have any questions or ideas please welcome to the Slack channel: [#mvikotlin](https://kotlinlang.slack.com/archives/C01403U1ZGW)

## Overview

### What is MVI
MVI stands for Model-View-Intent. It is an architectural pattern that utilizes unidirectional data flow. The data circulates between `Model` and `View` only in one direction - from `Model` to `View` and from `View` to `Model`.

<img src="docs/media/mvi.jpg" alt="MVI" width="300"/>

### What is MVIKotlin
MVIKotlin is a Kotlin Multiplatform framework that provides a way of (not only) writing
shared code using MVI pattern. It also includes powerful debug tools like
logging and time travel. The main functionality of the framework does not depend on any
reactive nor coroutines library. Extensions for [Reaktive](https://github.com/badoo/Reaktive)
and for [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) libraries are 
provided as separate modules.  

<img src="docs/media/mvikotlin.jpg" alt="MVIKotlin" width="600"/>


#### Responsibility
MVIKotlin does not bring or enforce any particular architecture. Its responsibility can be described as follows:

- To provide a single source of truth for `State` (the scope is not defined, it can be a whole app, a screen, a feature, or a part of a feature);
- To provide an abstraction for UI with efficient updates (however this is not obligatory, you can use whatever you want);
- To provide lifecycle aware connections (binding) between inputs and outputs (again this is not obligatory in any way).

Everything else is out of scope of the library, there are no definitions for "screens", "features", "modules", etc. Also, no particular reactive framework is enforced/exposed. This gives a lot of flexibility:

- MVIKotlin can be introduced incrementally (e.g. you can start using it in a small feature and then expand gradually);
- You can use/experiment with different architectures, approaches and/or libraries for navigation, UI, modularization, etc;
- Use whatever reactive framework you like or don't use it at all.

You can find one of the architecture options in the [samples](https://github.com/arkivanov/MVIKotlin/tree/master/sample). Again, this is just an example of one possible solution.

## Setup
Recommended minimum Gradle version is 5.3. Please read first the documentation about
[metadata publishing mode](https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#experimental-metadata-publishing-mode).

Add Bintray repository into your root build.gradle file:
```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/arkivanov/maven"
    }
}
```

There are a number of modules published:
- `mvikotlin` - core interfaces and functionality (multiplatform)
- `mvikotlin-main` - the main module with the default `Store` implementation (mutiplatform)
- `mvikotlin-logging` - logging functionality (mutiplatform)
- `mvikotlin-timetravel` - time travel feature (mutiplatform)
- `mvikotlin-extensions-reaktive` - extensions set for Reaktive library (multiplatform)
- `mvikotlin-extensions-coroutines` - extensions set for coroutines (multiplatform)
- `mvikotlin-extensions-androidx` - extensions set for Androidx (Android)
- `rx` - a tiny module with abstractions over rx and coroutines (multiplatform)

Add required modules to your module`s build.gradle file:
```groovy
implementation "com.arkivanov.mvikotlin:<module-name>:<version>"
```

## Features
* Multiplatform: Android, JVM, JavaScript, iosX64, iosArm64, macosX64, linuxX64
* Does not depend on any reactive library or coroutines
* Extensions for [Reaktive](https://github.com/badoo/Reaktive) library
* Extensions for [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
* Multithreading friendly (freezable in Kotlin Native if needed)
* Logging functionality with customizable logger and formatter
* Time travel feature:
  * Multiplatform for all supported targets
  * Plug-and-play UI for Android
  * Plug-and-play UI for iOS (copy-paste from the sample app)
  * Export/import events for Android
  * IDEA and Android Studio [plugin](https://plugins.jetbrains.com/plugin/14241-mvikotlin-time-travel) for Android apps
  * MacOS [client application](mvikotlin-timetravel-client/app-macos) for iOS and macOS apps

## Documentation

[https://arkivanov.github.io/MVIKotlin](https://arkivanov.github.io/MVIKotlin)

## Sample project

The sample project is a todo list with details view.
* Shared module using Reaktive is [here](sample/todo-reaktive)   
* Shared module using coroutines is [here](sample/todo-coroutines)
* Sample Android application with both Reaktive and coroutines implementations, plus logging and time travel is [here](sample/todo-app-android)
* Sample iOS application with Reaktive implementation only, plus logging and time travel is [here](sample/todo-app-ios)
* Sample JavaScript application with both Reaktive and coroutines implementations, plus logging and time travel is [here](sample/todo-app-js)

## Author
Twitter: [@arkann1985](https://twitter.com/arkann1985)

## Watch video (time travel, logs, debug, etc.)

#### Debugging Android application with MVIKotlin
[![Debugging Android application with MVIKotlin](https://img.youtube.com/vi/_bbxR503-u0/0.jpg)](https://youtu.be/_bbxR503-u0)

#### Debugging iOS application with MVIKotlin
[![Debugging iOS application with MVIKotlin](https://img.youtube.com/vi/MJCYQzeL-w8/0.jpg)](https://youtu.be/MJCYQzeL-w8)

#### Debugging Android application with IntelliJ IDEA time travel plugin
[![Debugging Android application with IntelliJ IDEA time travel plugin](https://img.youtube.com/vi/Tr2ayOcVU34/0.jpg)](https://youtu.be/Tr2ayOcVU34)

#### Debugging iOS application using MVIKotlin time travel client app
[![Debugging iOS application using MVIKotlin time travel client app](https://img.youtube.com/vi/rj6GwA2ZQkk/0.jpg)](https://youtu.be/rj6GwA2ZQkk)
