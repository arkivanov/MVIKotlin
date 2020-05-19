interface LifecycleConsumer<in T>: LifecycleOwner {

    val input: (T) -> Unit
}