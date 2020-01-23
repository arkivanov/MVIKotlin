object Deps {

    object Jetbrains {
        object Kotlin : Group(name = "org.jetbrains.kotlin") {
            private const val version = "1.3.61"

            object Plugin : Dependency(group = Kotlin, name = "kotlin-gradle-plugin", version = version)

            object StdLib {
                object Common : Dependency(group = Kotlin, name = "kotlin-stdlib-common", version = version)
                object Jdk7 : Dependency(group = Kotlin, name = "kotlin-stdlib-jdk7", version = version)
            }

            object Test {
                object Common : Dependency(group = Kotlin, name = "kotlin-test-common", version = version)
                object Js : Dependency(group = Kotlin, name = "kotlin-test-js", version = version)
                object Junit : Dependency(group = Kotlin, name = "kotlin-test-junit", version = version)
            }

            object TestAnnotations {
                object Common : Dependency(group = Kotlin, name = "kotlin-test-annotations-common", version = version)
            }
        }
    }

    object Android {
        object Tools {
            object Build : Group(name = "com.android.tools.build") {
                object Gradle : Dependency(group = Build, name = "gradle", version = "3.5.2")
            }
        }
    }

    object AndroidX {
        object Core : Group(name = "androidx.core") {
            object Ktx : Dependency(group = Core, name = "core-ktx", version = "1.1.0")
        }

        object RecyclerView : Group(name = "androidx.recyclerview") {
            object RecyclerView : Dependency(group = AndroidX.RecyclerView, name = "recyclerview", version = "1.1.0")
        }
    }

    object Badoo {
        object Reaktive : Group(name = "com.badoo.reaktive") {
            private const val version = "1.1.8"

            object Reaktive : Dependency(group = Badoo.Reaktive, name = "reaktive", version = version)
            object ReaktiveTesting : Dependency(group = Badoo.Reaktive, name = "reaktive-testing", version = version)
            object Utils : Dependency(group = Badoo.Reaktive, name = "utils", version = version)
        }
    }

    open class Group(val name: String)

    open class Dependency private constructor(
        private val notation: String
    ) : CharSequence by notation {
        constructor(group: Group, name: String, version: String) : this("${group.name}:$name:$version")

        override fun toString(): String = notation
    }
}
