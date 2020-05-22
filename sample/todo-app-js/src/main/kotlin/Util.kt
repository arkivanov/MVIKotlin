
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

const val ANIMATION_DURATION: Int = 200
const val DEBUG: Boolean = true

val storeFactoryInstance: StoreFactory = LoggingStoreFactory(delegate = DefaultStoreFactory)

val mFrameworkType: FrameworkType = FrameworkType.REAKTIVE

enum class FrameworkType {

    REAKTIVE, COROUTINES
}

