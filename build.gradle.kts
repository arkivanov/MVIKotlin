plugins {
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
}

dependencies {
    implementation(Deps.Jetbrains.Kotlin.Plugin)
    implementation(Deps.Android.Tools.Build.Gradle)
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://dl.bintray.com/badoo/maven")
    }
}
