import co.touchlab.kotlinxcodesync.SyncExtension
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
        js {
            nodejs()
            browser()

            compilations.all {
                compileKotlinTask.kotlinOptions {
                    metaInfo = true
                    sourceMap = true
                    sourceMapEmbedSources = "always"
                    moduleKind = "umd"
                    main = "call"
                }
            }
        }

        android {
            publishLibraryVariants("release", "debug")
        }

        jvm()
        linuxX64()
        iosX64()
        iosArm64()

        sourceSets {
            commonMain {
                dependencies {
                    implementation(Deps.Jetbrains.Kotlin.StdLib.Common)
                }
            }

            commonTest {
                dependencies {
                    implementation(Deps.Jetbrains.Kotlin.Test.Common)
                    implementation(Deps.Jetbrains.Kotlin.TestAnnotations.Common)
                }
            }

            jsNativeCommonMain {
                dependsOn(commonMain)
            }

            jsNativeCommonTest {
                dependsOn(commonTest)
            }

            jvmCommonMain {
                dependsOn(commonMain)

                dependencies {
                    implementation(Deps.Jetbrains.Kotlin.StdLib.Jdk7)
                }
            }

            jvmCommonTest {
                dependsOn(commonTest)

                dependencies {
                    implementation(Deps.Jetbrains.Kotlin.Test.Junit)
                }
            }

            jvmMain {
                dependsOn(jvmCommonMain)
            }

            jvmTest {
                dependsOn(jvmCommonTest)
            }

            androidMain {
                dependsOn(jvmCommonMain)
            }

            androidTest {
                dependsOn(jvmCommonTest)
            }

            jsMain {
                dependsOn(jsNativeCommonMain)

                dependencies {
                    implementation(Deps.Jetbrains.Kotlin.StdLib.Js)
                }
            }

            jsTest {
                dependsOn(jsNativeCommonTest)

                dependencies {
                    implementation(Deps.Jetbrains.Kotlin.Test.Js)
                }
            }

            nativeCommonMain {
                dependsOn(jsNativeCommonMain)
            }

            nativeCommonTest {
                dependsOn(jsNativeCommonTest)
            }

            linuxX64Main {
                dependsOn(nativeCommonMain)
            }

            linuxX64Test {
                dependsOn(nativeCommonTest)
            }

            darwinCommonMain {
                dependsOn(nativeCommonMain)
            }

            darwinCommonTest {
                dependsOn(nativeCommonTest)
            }

            iosCommonMain {
                dependsOn(darwinCommonMain)
            }

            iosCommonTest {
                dependsOn(darwinCommonTest)
            }

            iosX64Main {
                dependsOn(iosCommonMain)
            }

            iosX64Test {
                dependsOn(iosCommonTest)
            }

            iosArm64Main {
                dependsOn(iosCommonMain)
            }

            iosArm64Test {
                dependsOn(iosCommonTest)
            }
        }
    }
}

fun Project.setupAndroidSdkVersions() {
    android {
        compileSdkVersion(29)

        defaultConfig {
            targetSdkVersion(29)
            minSdkVersion(15)
        }
    }
}

fun Project.android(block: BaseExtension.() -> Unit) {
    extensions.getByType<BaseExtension>().block()
}

fun Project.kotlin(block: KotlinMultiplatformExtension.() -> Unit) {
    extensions.getByType<KotlinMultiplatformExtension>().block()
}

fun Project.setupXcodeSync() {
    plugins.apply("co.touchlab.kotlinxcodesync")

    extensions.getByType<SyncExtension>().run {
        projectPath = "../todo-app-ios/todo-app-ios.xcodeproj"
        target = "todo-app-ios"
    }
}

typealias SourceSets = NamedDomainObjectContainer<KotlinSourceSet>

fun KotlinMultiplatformExtension.sourceSets(block: SourceSets.() -> Unit) {
    sourceSets.block()
}

private fun SourceSets.getOrCreate(name: String): KotlinSourceSet = findByName(name) ?: create(name)

// common

val SourceSets.commonMain: KotlinSourceSet get() = getOrCreate("commonMain")

fun SourceSets.commonMain(block: KotlinSourceSet.() -> Unit) {
    commonMain.apply(block)
}

val SourceSets.commonTest: KotlinSourceSet get() = getOrCreate("commonTest")

fun SourceSets.commonTest(block: KotlinSourceSet.() -> Unit) {
    commonTest.apply(block)
}

// jsNativeCommon

val SourceSets.jsNativeCommonMain: KotlinSourceSet get() = getOrCreate("jsNativeCommonMain")

fun SourceSets.jsNativeCommonMain(block: KotlinSourceSet.() -> Unit) {
    jsNativeCommonMain.apply(block)
}

val SourceSets.jsNativeCommonTest: KotlinSourceSet get() = getOrCreate("jsNativeCommonTest")

fun SourceSets.jsNativeCommonTest(block: KotlinSourceSet.() -> Unit) {
    jsNativeCommonTest.apply(block)
}

// jvmCommon

val SourceSets.jvmCommonMain: KotlinSourceSet get() = getOrCreate("jvmCommonMain")

fun SourceSets.jvmCommonMain(block: KotlinSourceSet.() -> Unit) {
    jvmCommonMain.apply(block)
}

val SourceSets.jvmCommonTest: KotlinSourceSet get() = getOrCreate("jvmCommonTest")

fun SourceSets.jvmCommonTest(block: KotlinSourceSet.() -> Unit) {
    jvmCommonTest.apply(block)
}

// jvm

val SourceSets.jvmMain: KotlinSourceSet get() = getOrCreate("jvmMain")

fun SourceSets.jvmMain(block: KotlinSourceSet.() -> Unit) {
    jvmMain.apply(block)
}

val SourceSets.jvmTest: KotlinSourceSet get() = getOrCreate("jvmTest")

fun SourceSets.jvmTest(block: KotlinSourceSet.() -> Unit) {
    jvmTest.apply(block)
}

// android

val SourceSets.androidMain: KotlinSourceSet get() = getOrCreate("androidMain")

fun SourceSets.androidMain(block: KotlinSourceSet.() -> Unit) {
    androidMain.apply(block)
}

val SourceSets.androidTest: KotlinSourceSet get() = getOrCreate("androidTest")

fun SourceSets.androidTest(block: KotlinSourceSet.() -> Unit) {
    androidTest.apply(block)
}

// js

val SourceSets.jsMain: KotlinSourceSet get() = getOrCreate("jsMain")

fun SourceSets.jsMain(block: KotlinSourceSet.() -> Unit) {
    jsMain.apply(block)
}

val SourceSets.jsTest: KotlinSourceSet get() = getOrCreate("jsTest")

fun SourceSets.jsTest(block: KotlinSourceSet.() -> Unit) {
    jsTest.apply(block)
}

// nativeCommon

val SourceSets.nativeCommonMain: KotlinSourceSet get() = getOrCreate("nativeCommonMain")

fun SourceSets.nativeCommonMain(block: KotlinSourceSet.() -> Unit) {
    nativeCommonMain.apply(block)
}

val SourceSets.nativeCommonTest: KotlinSourceSet get() = getOrCreate("nativeCommonTest")

fun SourceSets.nativeCommonTest(block: KotlinSourceSet.() -> Unit) {
    nativeCommonTest.apply(block)
}

// linuxX64

val SourceSets.linuxX64Main: KotlinSourceSet get() = getOrCreate("linuxX64Main")

fun SourceSets.linuxX64Main(block: KotlinSourceSet.() -> Unit) {
    linuxX64Main.apply(block)
}

val SourceSets.linuxX64Test: KotlinSourceSet get() = getOrCreate("linuxX64Test")

fun SourceSets.linuxX64Test(block: KotlinSourceSet.() -> Unit) {
    linuxX64Test.apply(block)
}

// darwinCommon

val SourceSets.darwinCommonMain: KotlinSourceSet get() = getOrCreate("darwinCommonMain")

fun SourceSets.darwinCommonMain(block: KotlinSourceSet.() -> Unit) {
    darwinCommonMain.apply(block)
}

val SourceSets.darwinCommonTest: KotlinSourceSet get() = getOrCreate("darwinCommonTest")

fun SourceSets.darwinCommonTest(block: KotlinSourceSet.() -> Unit) {
    darwinCommonTest.apply(block)
}

// iosCommon

val SourceSets.iosCommonMain: KotlinSourceSet get() = getOrCreate("iosCommonMain")

fun SourceSets.iosCommonMain(block: KotlinSourceSet.() -> Unit) {
    iosCommonMain.apply(block)
}

val SourceSets.iosCommonTest: KotlinSourceSet get() = getOrCreate("iosCommonTest")

fun SourceSets.iosCommonTest(block: KotlinSourceSet.() -> Unit) {
    iosCommonTest.apply(block)
}

// iosX64

val SourceSets.iosX64Main: KotlinSourceSet get() = getOrCreate("iosX64Main")

fun SourceSets.iosX64Main(block: KotlinSourceSet.() -> Unit) {
    iosX64Main.apply(block)
}

val SourceSets.iosX64Test: KotlinSourceSet get() = getOrCreate("iosX64Test")

fun SourceSets.iosX64Test(block: KotlinSourceSet.() -> Unit) {
    iosX64Test.apply(block)
}

// iosArm64

val SourceSets.iosArm64Main: KotlinSourceSet get() = getOrCreate("iosArm64Main")

fun SourceSets.iosArm64Main(block: KotlinSourceSet.() -> Unit) {
    iosArm64Main.apply(block)
}

val SourceSets.iosArm64Test: KotlinSourceSet get() = getOrCreate("iosArm64Test")

fun SourceSets.iosArm64Test(block: KotlinSourceSet.() -> Unit) {
    iosArm64Test.apply(block)
}
