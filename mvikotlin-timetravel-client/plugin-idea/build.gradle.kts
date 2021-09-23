import com.arkivanov.gradle.disableCompilationsIfNeeded
import com.arkivanov.gradle.isCompilationAllowed
import org.jetbrains.intellij.tasks.BuildSearchableOptionsTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.intellij")
    kotlin("jvm")
}

group = "org.arkivanov.mvikotlin.plugin.idea.timetravel"
version = deps.versions.mvikotlin.get()

kotlin {
    target {
        disableCompilationsIfNeeded()
    }
}

tasks.withType<BuildSearchableOptionsTask>().configureEach {
    enabled = kotlin.target.isCompilationAllowed
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":mvikotlin-timetravel-client:client-internal"))
    implementation(project(":mvikotlin-timetravel-proto-internal"))
    implementation(project(":mvikotlin"))
    implementation(project(":mvikotlin-main"))
    implementation(deps.reaktive.reaktive)
    implementation(deps.russhwolf.multiplatformSettings)
}

tasks {
    patchPluginXml {
        sinceBuild("193")
    }
}

intellij {
    version = "2019.3"
    updateSinceUntilBuild = false
}
