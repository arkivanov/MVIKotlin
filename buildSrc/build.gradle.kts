plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(deps.kotlin.kotlinGradlePlug)
    implementation(deps.android.gradle)

    // Workaround, see https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(deps.javaClass.superclass.protectionDomain.codeSource.location))
}

