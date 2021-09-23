package com.arkivanov.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin
import kotlin.reflect.KClass

inline fun <reified T : Any> Project.withExtension(block: T.() -> Unit) {
    extensions.getByType<T>().block()
}

fun Project.kotlin(block: KotlinMultiplatformExtension.() -> Unit) {
    withExtension(block)
}

internal fun KotlinSourceSetContainer.sourceSets(block: NamedDomainObjectContainer<KotlinSourceSet>.() -> Unit) {
    sourceSets.block()
}

internal inline fun <reified T : Target> Project.doIfTargetEnabled(block: (T) -> Unit) {
    enabledTargets
        .filterIsInstance<T>()
        .singleOrNull()
        ?.also(block)
}

internal inline fun <reified T : Target> Project.isTargetEnabled(): Boolean =
    enabledTargets.any { it is T }

internal inline fun Project.isTargetEnabled(block: (Target) -> Boolean): Boolean =
    enabledTargets.any(block)

internal val Target.isJava: Boolean
    get() =
        when (this) {
            is Target.Android,
            is Target.Jvm -> true
            is Target.Linux,
            is Target.Ios,
            is Target.WatchOs,
            is Target.MacOs,
            is Target.Js -> false
        }

internal val Target.isNative: Boolean
    get() =
        when (this) {
            is Target.Linux,
            is Target.Ios,
            is Target.WatchOs,
            is Target.MacOs -> true
            is Target.Android,
            is Target.Jvm,
            is Target.Js -> false
        }

internal val Target.isDarwin: Boolean
    get() =
        when (this) {
            is Target.Ios,
            is Target.WatchOs,
            is Target.MacOs -> true
            is Target.Android,
            is Target.Jvm,
            is Target.Linux,
            is Target.Js -> false
        }

@Suppress("UNCHECKED_CAST")
internal var Project.enabledTargets: List<Target>
    get() = extra.get("enabled_targets") as List<Target>
    set(value) {
        check(!extra.has("enabled_targets")) { "Targets can be enabled only once" }
        extra.set("enabled_targets", value)
    }

fun KotlinTarget.disableCompilationsIfNeeded() {
    if (!isCompilationAllowed) {
        disableCompilations()
    }
}

fun KotlinTarget.disableCompilations() {
    compilations.configureEach {
        compileKotlinTask.enabled = false
    }
}

val KotlinTarget.isCompilationAllowed: Boolean
    get() =
        (name == KotlinMultiplatformPlugin.METADATA_TARGET_NAME) ||
            isTargetCompilationAllowed(targetClass ?: error("No target class found for $this"))

private val KotlinTarget.targetClass: KClass<out Target>?
    get() =
        when (platformType) {
            KotlinPlatformType.androidJvm -> Target.Android::class
            KotlinPlatformType.jvm -> Target.Jvm::class
            KotlinPlatformType.js -> Target.Js::class

            KotlinPlatformType.native ->
                Target::class.sealedSubclasses.find { clazz ->
                    name.startsWith(prefix = requireNotNull(clazz.simpleName), ignoreCase = true)
                }

            KotlinPlatformType.common -> null
        }

fun isTargetCompilationAllowed(clazz: KClass<out Target>): Boolean {
    if (System.getProperty("split_targets") == null) {
        return true
    }

    val os = OperatingSystem.current()

    return when {
        os.isLinux -> clazz in Target.LINUX_SPLIT_CLASSES
        os.isMacOsX -> clazz in Target.MACOS_SPLIT_CLASSES
//        os.isLinux -> clazz in Target.MACOS_SPLIT_CLASSES
//        os.isMacOsX -> clazz in Target.LINUX_SPLIT_CLASSES
        else -> error("Unsupported OS type: $os")
    }
}
