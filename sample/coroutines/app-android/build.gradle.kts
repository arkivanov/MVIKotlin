import com.arkivanov.gradle.setupAndroidApp

plugins {
    id("com.arkivanov.gradle.setup")
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
}

setupAndroidApp(
    applicationId = "com.arkivanov.mvikotlin.sample.coroutines.app",
    versionCode = 1,
    versionName = "1.0",
)

android {
    namespace = "com.arkivanov.mvikotlin.sample.coroutines.app"

    packagingOptions {
        exclude("META-INF/*")
    }
}

dependencies {
    implementation(project(":mvikotlin-main"))
    implementation(project(":mvikotlin-timetravel"))
    implementation(project(":mvikotlin-logging"))
    implementation(project(":mvikotlin-extensions-coroutines"))
    implementation(project(":sample:coroutines:shared"))
    implementation(deps.androidx.appcompat.appcompat)
    implementation(deps.androidx.recyclerview.recyclerview)
    implementation(deps.androidx.constraintlayout.constraintlayout)
    implementation(deps.androidx.drawerlayout.drawerlayout)
    implementation(deps.androidx.core.coreKtx)
    implementation(deps.androidx.lifecycle.lifecycleCommonJava8)
}
