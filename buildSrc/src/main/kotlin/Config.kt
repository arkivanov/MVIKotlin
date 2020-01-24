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

        jvm()
        linuxX64()

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

            nativeCommonMain {
                dependsOn(commonMain)
            }

            nativeCommonTest {
                dependsOn(commonTest)
            }

            linuxX64Main {
                dependsOn(nativeCommonMain)
            }

            linuxX64Test {
                dependsOn(nativeCommonTest)
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

// jvmCommon

val SourceSets.jvmCommonMain: KotlinSourceSet get() = getOrCreate("jvmCommonMain")

fun SourceSets.jvmCommonMain(block: KotlinSourceSet.() -> Unit) {
    jvmCommonMain.apply(block)
}

val SourceSets.jvmCommonTest: KotlinSourceSet get() = getOrCreate("jvmCommonTest")

fun SourceSets.jvmCommonTest(block: KotlinSourceSet.() -> Unit) {
    jvmCommonTest.apply(block)
}

// JVM

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
