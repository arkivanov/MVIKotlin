import com.arkivanov.mvikotlin.rx.Disposable

@Suppress("FunctionName") // Factory function
inline fun Disposable(crossinline onDispose: Disposable.() -> Unit = {}): Disposable =
    object : Disposable {
        override var isDisposed: Boolean = false

        override fun dispose() {
            isDisposed = true
            onDispose()
        }
    }
