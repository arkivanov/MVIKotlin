plugins {
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
}

dependencies {
    implementation(Deps.Jetbrains.Kotlin.Plugin.Gradle)
    implementation(Deps.Android.Tools.Build.Gradle)
    implementation(Deps.TouchLab.KotlinXcodeSync)
    implementation(Deps.Codearte.Gradle.Nexus.StagingPlugin)
}

kotlin {
    // Add Deps to compilation, so it will become available in main project
    sourceSets.getByName("main").kotlin.srcDir("buildSrc/src/main/kotlin")
}
