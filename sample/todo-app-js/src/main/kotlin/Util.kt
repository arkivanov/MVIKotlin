import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory

const val DEBUG: Boolean = true

val storeFactoryInstance: StoreFactory =
    if (DEBUG) {
        LoggingStoreFactory(delegate = TimeTravelStoreFactory())
    } else {
        DefaultStoreFactory()
    }

val mFrameworkType: FrameworkType = FrameworkType.REAKTIVE

enum class FrameworkType {
    REAKTIVE, COROUTINES
}

