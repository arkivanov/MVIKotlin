# MVIDroid
MVI framework designed for Android

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
    implementation 'com.github.arkivanov:mvidroid:1.0.1'
}
```
### Maven:
Respository:
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
    <groupId>com.github.arkivanov</groupId>
    <artifactId>mvidroid</artifactId>
    <version>1.0.1</version>
</dependency>
```
