plugins {
    id("com.arkivanov.gradle.setup")
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
}

setupAndroidApp {
    androidApp(
        applicationId = "com.arkivanov.rxkotlin.sample.todo.android",
        versionCode = 1,
        versionName = "1.0",
    )
}

android {
    packagingOptions {
        exclude("META-INF/*")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":mvikotlin"))
    implementation(project(":mvikotlin-main"))
    implementation(project(":mvikotlin-timetravel"))
    implementation(project(":mvikotlin-logging"))
    implementation(project(":sample:todo-reaktive"))
    implementation(project(":sample:todo-coroutines"))
    implementation(deps.androidx.appcompat.appcompat)
    implementation(deps.androidx.recyclerview.recyclerview)
    implementation(deps.androidx.constraintlayout.constraintlayout)
    implementation(deps.androidx.drawerlayout.drawerlayout)
    implementation(deps.androidx.core.coreKtx)
    implementation(deps.androidx.lifecycle.lifecycleCommonJava8)
    implementation(deps.kotlinx.kotlinxCoroutinesCore)
    implementation(deps.kotlinx.kotlinxCoroutinesAndroid)
    implementation(deps.essenty.instanceKeeper)
}
