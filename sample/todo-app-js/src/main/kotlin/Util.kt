import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory

const val ANIMATION_DURATION = 200
const val DEBUG = true

val storeFactoryInstance =
    if (DEBUG) {
        LoggingStoreFactory(delegate = TimeTravelStoreFactory(fallback = DefaultStoreFactory))
    } else {
        DefaultStoreFactory
    }

val frameWorkType = FrameworkType.REAKTIVE

enum class FrameworkType {

    REAKTIVE, COROUTINES
}
