import co.touchlab.kotlinxcodesync.SyncExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithSimulatorTests

enum class BuildType {
    ALL, METADATA, NON_NATIVE, LINUX, IOS, MAC_OS
}

val ExtensionAware.buildType: BuildType
    get() =
        find("build_type")
            ?.toString()
            ?.let(BuildType::valueOf)
            ?: BuildType.ALL

private fun ExtensionAware.find(key: String) =
    if (extra.has(key)) extra.get(key) else null

interface BuildTarget {

    interface NonNative : BuildTarget

    interface Native : BuildTarget

    interface Darwin : Native

    interface Ios : Darwin

    interface Linux : Native

    object Android : NonNative
    object Jvm : NonNative
    object Js : NonNative
    object IosX64 : Ios
    object IosArm64 : Ios
    object MacOsX64 : Darwin
    object LinuxX64 : Linux
}

private val ALL_BUILD_TARGETS =
    setOf(
        BuildTarget.Android,
        BuildTarget.Jvm,
        BuildTarget.Js,
        BuildTarget.IosX64,
        BuildTarget.IosArm64,
        BuildTarget.MacOsX64,
        BuildTarget.LinuxX64
    )

private val BUILD_TYPE_TO_BUILD_TARGETS: Map<BuildType, Set<BuildTarget>> =
    mapOf(
        BuildType.ALL to ALL_BUILD_TARGETS,
        BuildType.METADATA to ALL_BUILD_TARGETS,
        BuildType.NON_NATIVE to setOf(BuildTarget.Android, BuildTarget.Jvm, BuildTarget.Js),
        BuildType.LINUX to setOf(BuildTarget.LinuxX64),
        BuildType.IOS to setOf(BuildTarget.IosX64, BuildTarget.IosArm64),
        BuildType.MAC_OS to setOf(BuildTarget.MacOsX64)
    )

val BuildType.buildTargets: Set<BuildTarget> get() = requireNotNull(BUILD_TYPE_TO_BUILD_TARGETS[this])

@Suppress("UNCHECKED_CAST")
var ExtensionAware.buildTargets: Set<BuildTarget>
    get() = if (extra.has("project_build_targets")) extra["project_build_targets"] as Set<BuildTarget> else ALL_BUILD_TARGETS
    set(value) {
        extra["project_build_targets"] = value
    }

inline fun <reified T : BuildTarget> ExtensionAware.isBuildTargetAvailable(): Boolean =
    buildType.buildTargets.any { it is T } && buildTargets.any { it is T }

inline fun <reified T : BuildTarget> ExtensionAware.doIfBuildTargetAvailable(block: () -> Unit) {
    if (isBuildTargetAvailable<T>()) {
        block()
    }
}

fun Project.setupMultiplatform() {
    plugins.apply("kotlin-multiplatform")

    doIfBuildTargetAvailable<BuildTarget.Android> {
        plugins.apply("com.android.library")

        setupAndroidSdkVersions()
    }

    kotlin {
        doIfBuildTargetAvailable<BuildTarget.Js> {
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
        }

        doIfBuildTargetAvailable<BuildTarget.Android> {
            android {
                publishLibraryVariants("release", "debug")
            }
        }

        doIfBuildTargetAvailable<BuildTarget.Jvm> {
            jvm()
        }

        doIfBuildTargetAvailable<BuildTarget.LinuxX64> {
            linuxX64()
        }

        doIfBuildTargetAvailable<BuildTarget.IosX64> {
            iosX64()
        }

        doIfBuildTargetAvailable<BuildTarget.IosArm64> {
            iosArm64()
        }

        doIfBuildTargetAvailable<BuildTarget.MacOsX64> {
            macosX64()
        }

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

            jsNativeCommonMain.dependsOn(commonMain)
            jsNativeCommonTest.dependsOn(commonTest)

            jvmNativeCommonMain.dependsOn(commonMain)
            jvmNativeCommonTest.dependsOn(commonTest)

            jvmCommonMain {
                dependsOn(jvmNativeCommonMain)

                dependencies {
                    implementation(Deps.Jetbrains.Kotlin.StdLib.Jdk7)
                }
            }

            jvmCommonTest {
                dependsOn(jvmNativeCommonTest)

                dependencies {
                    implementation(Deps.Jetbrains.Kotlin.Test.Junit)
                }
            }

            jvmMain.dependsOn(jvmCommonMain)
            jvmTest.dependsOn(jvmCommonTest)

            androidMain.dependsOn(jvmCommonMain)
            androidTest.dependsOn(jvmCommonTest)

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
                dependsOn(jvmNativeCommonMain)
            }

            nativeCommonTest {
                dependsOn(jsNativeCommonTest)
                dependsOn(jvmNativeCommonTest)
            }

            linuxX64Main.dependsOn(nativeCommonMain)
            linuxX64Test.dependsOn(nativeCommonTest)

            darwinCommonMain.dependsOn(nativeCommonMain)
            darwinCommonTest.dependsOn(nativeCommonTest)

            iosCommonMain.dependsOn(darwinCommonMain)
            iosCommonTest.dependsOn(darwinCommonTest)

            iosX64Main.dependsOn(iosCommonMain)
            iosX64Test.dependsOn(iosCommonTest)

            iosArm64Main.dependsOn(iosCommonMain)
            iosArm64Test.dependsOn(iosCommonTest)

            macosX64Main.dependsOn(darwinCommonMain)
            macosX64Test.dependsOn(darwinCommonTest)
        }
    }
}

fun Project.setupPublication() {
    plugins.apply("maven-publish")

    group = "com.arkivanov.mvikotlin"
    version = property("mvikotlin.version") as String

    val userId = "arkivanov"
    val userName = "Arkadii Ivanov"
    val userEmail = "arkann1985@gmail.com"
    val githubUrl = "https://github.com/arkivanov/MVIKotlin"
    val githubScmUrl = "scm:git:git://github.com/arkivanov/MVIKotlin.git"
    val isMetadataBuildType = buildType === BuildType.METADATA
    val metadataPublicationNames = setOf(KotlinMultiplatformPlugin.METADATA_TARGET_NAME, "kotlinMultiplatform")

    extensions.getByType<PublishingExtension>().run {
        publications.withType<MavenPublication>().all {
            pom {
                withXml {
                    asNode().apply {
                        appendNode("name", "MVIKotlin")
                        appendNode("description", "Kotlin Multiplatform MVI framework")
                        appendNode("url", githubUrl)
                    }
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set(userId)
                        name.set(userName)
                        email.set(userEmail)
                    }
                }
                scm {
                    url.set(githubUrl)
                    connection.set(githubScmUrl)
                    developerConnection.set(githubScmUrl)
                }
            }
        }

        afterEvaluate {
            tasks.withType<PublishToMavenRepository>().forEach { task ->
                task.enabled = (task.publication.name in metadataPublicationNames) == isMetadataBuildType
            }
        }

        repositories {
            maven {
                url = uri("https://api.bintray.com/maven/arkivanov/maven/mvikotlin/;publish=0;override=1")
                credentials {
                    username = userId
                    password = findProperty("bintray_api_key")?.toString()
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
            minSdkVersion(15)
        }
    }
}

// Workaround since iosX64() function is not resolved if used in a module with Kotlin 1.3.70
fun KotlinMultiplatformExtension.iosX64Compat(): KotlinNativeTarget = iosX64()

// Workaround since iosArm64() function is not resolved if used in a module with Kotlin 1.3.70
fun KotlinMultiplatformExtension.iosArm64Compat(): KotlinNativeTarget = iosArm64()

// Workaround since macosX64() function is not resolved if used in a module with Kotlin 1.3.70
fun KotlinMultiplatformExtension.macosX64Compat(): KotlinNativeTarget = macosX64()

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

// jvmNativeCommon

val SourceSets.jvmNativeCommonMain: KotlinSourceSet get() = getOrCreate("jvmNativeCommonMain")

fun SourceSets.jvmNativeCommonMain(block: KotlinSourceSet.() -> Unit) {
    jvmNativeCommonMain.apply(block)
}

val SourceSets.jvmNativeCommonTest: KotlinSourceSet get() = getOrCreate("jvmNativeCommonTest")

fun SourceSets.jvmNativeCommonTest(block: KotlinSourceSet.() -> Unit) {
    jvmNativeCommonTest.apply(block)
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

// macosX64

val SourceSets.macosX64Main: KotlinSourceSet get() = getOrCreate("macosX64Main")

fun SourceSets.macosX64Main(block: KotlinSourceSet.() -> Unit) {
    macosX64Main.apply(block)
}

val SourceSets.macosX64Test: KotlinSourceSet get() = getOrCreate("macosX64Test")

fun SourceSets.macosX64Test(block: KotlinSourceSet.() -> Unit) {
    macosX64Test.apply(block)
}
