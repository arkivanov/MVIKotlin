interface LifecycledConsumer<in T>: LifecycleOwner {

    val input: (T) -> Unit
}
