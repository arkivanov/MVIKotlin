package com.arkivanov.mvikotlin.sample.reaktive.app

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.coroutines.shared.DefaultDispatchers
import com.arkivanov.mvikotlin.sample.coroutines.shared.TodoDispatchers
import com.arkivanov.mvikotlin.sample.database.DefaultTodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import csstype.AlignItems
import csstype.Display
import csstype.JustifyContent
import csstype.px
import csstype.vh
import csstype.vw
import kotlinx.browser.document
import kotlinx.coroutines.flow.MutableSharedFlow
import mui.material.Box
import mui.system.sx
import react.FC
import react.Props
import react.VFC
import react.create
import react.dom.client.createRoot
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter
import react.router.useNavigate
import react.router.useParams
import react.useMemo

fun main() {
    createRoot(document.getElementById("app")!!).render(
        Root.create {
            storeFactory = DefaultStoreFactory()
            database = DefaultTodoDatabase()
            dispatchers = DefaultDispatchers
        }
    )
}

external interface RootProps : Props {
    var storeFactory: StoreFactory
    var database: TodoDatabase
    var dispatchers: TodoDispatchers
}

val Root: FC<RootProps> = FC { props ->
    val mainInput = useMemo { MutableSharedFlow<MainInput>(extraBufferCapacity = Int.MAX_VALUE) }

    Box {
        sx {
            display = Display.flex
            justifyContent = JustifyContent.center
            alignItems = AlignItems.center
            width = 100.vw
            height = 100.vh
        }

        Box {
            sx {
                width = 640.px
                height = 480.px
            }

            BrowserRouter {
                Routes {
                    Route {
                        index = true
                        element = VFC main@{
                            val navigate = useNavigate()

                            MainComponent {
                                storeFactory = props.storeFactory
                                database = props.database
                                dispatchers = props.dispatchers
                                input = mainInput
                                onItemSelected = { id -> navigate(to = "/$id") }
                            }
                        }.create()
                    }

                    Route {
                        path = "/:itemId"
                        element = VFC details@{
                            val params = useParams()
                            val navigate = useNavigate()

                            DetailsComponent {
                                database = props.database
                                storeFactory = props.storeFactory
                                dispatchers = props.dispatchers
                                itemId = requireNotNull(params["itemId"])
                                onFinished = { navigate(delta = -1) }
                                onItemChanged = { id, data -> mainInput.tryEmit(MainInput.ItemChanged(id = id, data = data)) }
                                onItemDeleted = { id ->
                                    mainInput.tryEmit(MainInput.ItemDeleted(id = id))
                                    navigate(delta = -1)
                                }
                            }
                        }.create()
                    }
                }
            }
        }
    }
}
