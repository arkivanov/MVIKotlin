import com.android.build.gradle.BaseExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun Project.setupMultiplatform() {
    plugins.apply("kotlin-multiplatform")
    plugins.apply("com.android.library")

    group = "com.arkivanov.mvikotlin"
    version = "0.0.1"

    setupAndroidSdkVersions()

    kotlin {
        android {
            publishLibraryVariants("release", "debug")
        }

        sourceSets {
            commonMain {
                dependencies {
                    implementation(Deps.Jetbrains.Kotlin.StdLib.Common)
                }
            }

            androidMain {
                dependencies {
                    implementation(Deps.Jetbrains.Kotlin.StdLib.Jdk7)
                }
            }
        }
    }
}

fun Project.setupAndroidSdkVersions() {
    android {
        compileSdkVersion(29)

        defaultConfig {
            targetSdkVersion(29)
            minSdkVersion(14)
        }
    }
}

fun Project.android(block: BaseExtension.() -> Unit) {
    extensions.getByType<BaseExtension>().block()
}

fun Project.kotlin(block: KotlinMultiplatformExtension.() -> Unit) {
    extensions.getByType<KotlinMultiplatformExtension>().block()
}

typealias SourceSets = NamedDomainObjectContainer<KotlinSourceSet>

fun KotlinMultiplatformExtension.sourceSets(block: SourceSets.() -> Unit) {
    sourceSets.block()
}

val SourceSets.commonMain: KotlinSourceSet get() = getOrCreate("commonMain")

fun SourceSets.commonMain(block: KotlinSourceSet.() -> Unit) {
    commonMain.apply(block)
}

val SourceSets.androidMain: KotlinSourceSet get() = getOrCreate("androidMain")

fun SourceSets.androidMain(block: KotlinSourceSet.() -> Unit) {
    androidMain.apply(block)
}

private fun SourceSets.getOrCreate(name: String): KotlinSourceSet = findByName(name) ?: create(name)
