import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

val storeFactoryInstance: StoreFactory = LoggingStoreFactory(delegate = DefaultStoreFactory)

val mFrameworkType: FrameworkType = FrameworkType.REAKTIVE

enum class FrameworkType {

    REAKTIVE, COROUTINES
}

