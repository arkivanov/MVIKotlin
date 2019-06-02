[![](https://jitpack.io/v/arkivanov/MVIDroid.svg)](https://jitpack.io/#arkivanov/MVIDroid)

## What is MVIDroid

MVIDroid is a framework written 100% in Kotlin which brings MVI pattern
to Android. It is specially designed and optimized for Android platform,
very lightweight and efficient. Includes powerful debug tools like
logging and time travel. Also MVIDroid provides a pattern for decoupling
of business logic and UI, so you can make them completely independent
from each other.

![MVI](docs/media/mvi.jpg)

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
    implementation 'com.github.arkivanov.mvidroid:mvidroid:1.2.6'
    implementation 'com.github.arkivanov.mvidroid:mvidroid-debug:1.2.6'
}
```

### Maven:

Repository:
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
    <version>1.2.6</version>
</dependency>
<dependency>
    <groupId>com.github.arkivanov.mvidroid</groupId>
    <artifactId>mvidroid-debug</artifactId>
    <version>1.2.6/version>
</dependency>
```

## Documentation

You can find documentation [here](docs/index.md).

## Sample project

You can find a sample project in the repo: todo list with details view.
The sample project covers most of the cases:
- Multiple `Stores`
- Multiple screens
- `Stores` shared between `Components`
- Delayed operations and their cancellation
- `Labels`
- `View` with `RecycleView` and `Adapter`
- Logging in debug build
- Time travel drawer in debug build with export/import (right side of the screen)
