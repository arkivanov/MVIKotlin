<img src="https://raw.githubusercontent.com/arkivanov/MVIKotlin/master/docs/media/logo/landscape/png/mvikotlin_coloured.png" height="64">

[![Maven Central](https://img.shields.io/maven-central/v/com.arkivanov.mvikotlin/mvikotlin?color=blue)](https://search.maven.org/artifact/com.arkivanov.mvikotlin/mvikotlin)
[![License](https://img.shields.io/badge/License-Apache/2.0-blue.svg)](https://github.com/arkivanov/MVIKotlin/blob/master/LICENSE)
[![kotlinlang|MVIKotlin](https://img.shields.io/badge/kotlinlang-mvikotlin-blue?logo=slack)](https://kotlinlang.slack.com/archives/C01403U1ZGW)

Should you have any questions or ideas please welcome to the Slack channel: [#mvikotlin](https://kotlinlang.slack.com/archives/C01403U1ZGW)

## ⚡⚡⚡ Why is this repository a fork?

Having spent 5 years working on a variety of projects for Badoo/Bumble, I’m now off to another adventure. As part of that transition I was asked to transfer this repository to [Badoo GitHub account](https://github.com/badoo).

Now I **continue my work** on this project **as a fork**.

There should be no breaking changes related to this transfer. Most of the external links should not be broken. The repository link is also the same: [arkivanov/MVIKotlin](https://github.com/arkivanov/MVIKotlin). Please file an issue in this repository, if you think something is broken or does not work properly.

Here is what is mostly affected by the transfer:

- All the stars were transferred
- Search in the code will not work **as long as this repository has less stars than the parent** (your help is needed!)
- All the Issues and Discussions were transferred as well. I will do all my best to fill the gap here.
- All pull requests with all the comment history are also gone.

I will continue doing all my best for this project and for the community! Business as usual!

Additional resources:

- [A thread on Hacker News](https://news.ycombinator.com/item?id=29447854)

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

There are a number of modules published to Maven Central:

- `mvikotlin` - core interfaces and functionality (multiplatform)
- `mvikotlin-main` - the main module with the default `Store` implementation (mutiplatform)
- `mvikotlin-logging` - logging functionality (mutiplatform)
- `mvikotlin-timetravel` - time travel feature (mutiplatform)
- `mvikotlin-extensions-reaktive` - extensions set for Reaktive library (multiplatform)
- `mvikotlin-extensions-coroutines` - extensions set for coroutines (multiplatform)
- ~~`keepers` - provides `StateKeeper` and `InstanceKeeper` API for state preservation and objects retaining~~ (deprecated, see the [documentation](https://arkivanov.github.io/MVIKotlin/state_preservation.html))
- `rx` - a tiny module with abstractions over rx and coroutines (multiplatform)

Add required modules to your module`s build.gradle file:
```groovy
implementation "com.arkivanov.mvikotlin:<module-name>:<version>"
```

### IDEA Live Templates

To speed up the creation of new Stores, you can use the following [IDEA Live Templates](https://gist.github.com/arkivanov/34bb84e73e56c22a4e7c752421d5f02c).

## Features
* Multiplatform: Android, JVM, JavaScript, iosX64, iosArm64, macosX64, linuxX64
* Does not depend on any reactive library or coroutines
* Extensions for [Reaktive](https://github.com/badoo/Reaktive) library
* Extensions for [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
* Multithreading friendly (freezable in Kotlin Native if needed)
* Lifecycle-aware connections (bindins) between inputs and outputs
* Logging functionality with customizable logger and formatter
* Time travel feature:
    * Multiplatform for all supported targets
    * Plug-and-play UI for Android
    * Plug-and-play UI for iOS (copy-paste from the sample app)
    * Export/import events for Android
    * IntelliJ IDEA and Android Studio [plugin](https://plugins.jetbrains.com/plugin/14241-mvikotlin-time-travel) for Android apps
    * Desktop [client application](mvikotlin-timetravel-client/app-desktop) for Android, Java and native Apple (iOS, watchOS, tvOS, macOS) apps

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

If you like this project you can always <a href="https://www.buymeacoffee.com/arkivanov" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-blue.png" alt="Buy Me A Coffee" height=32></a> ;-)

## Watch video (time travel, logs, debug, etc.)

#### Debugging Android application with MVIKotlin
[![Debugging Android application with MVIKotlin](https://img.youtube.com/vi/_bbxR503-u0/0.jpg)](https://youtu.be/_bbxR503-u0)

#### Debugging iOS application with MVIKotlin
[![Debugging iOS application with MVIKotlin](https://img.youtube.com/vi/MJCYQzeL-w8/0.jpg)](https://youtu.be/MJCYQzeL-w8)

#### Debugging Android application with IntelliJ IDEA time travel plugin
[![Debugging Android application with IntelliJ IDEA time travel plugin](https://img.youtube.com/vi/Tr2ayOcVU34/0.jpg)](https://youtu.be/Tr2ayOcVU34)

#### Debugging iOS application using MVIKotlin time travel client app
[![Debugging iOS application using MVIKotlin time travel client app](https://img.youtube.com/vi/rj6GwA2ZQkk/0.jpg)](https://youtu.be/rj6GwA2ZQkk)
