plugins {
    `kotlin-dsl`
}

buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(Deps.Jetbrains.Kotlin.Plugin)
        classpath(Deps.Android.Tools.Build.Gradle)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://dl.bintray.com/badoo/maven")
    }
}
