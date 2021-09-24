package com.arkivanov.gradle

import kotlin.reflect.KClass

sealed class Target {

    object Android : Target()
    object Jvm : Target()
    object Linux : Target()
    object Ios : Target()
    object WatchOs : Target()
    object MacOs : Target()

    class Js(val mode: Mode = Mode.BOTH) : Target() {
        enum class Mode {
            BOTH, IR, LEGACY
        }
    }

    companion object {
        val ALL_DEFAULT: List<Target> =
            listOf(
                Android,
                Jvm,
                Linux,
                Ios,
                WatchOs,
                MacOs,
                Js(),
            )

        val LINUX_SPLIT_CLASSES: List<KClass<out Target>> =
            listOf(
                Android::class,
                Jvm::class,
                Linux::class,
                Js::class,
            )

        val MACOS_SPLIT_CLASSES: List<KClass<out Target>> =
            listOf(
                Ios::class,
                WatchOs::class,
                MacOs::class,
            )

        init {
            check(ALL_DEFAULT.size == Target::class.sealedSubclasses.size) {
                "Not all targets are listed"
            }

            check((LINUX_SPLIT_CLASSES.size + MACOS_SPLIT_CLASSES.size) == Target::class.sealedSubclasses.size) {
                "Not all targets are listed"
            }
        }
    }
}
