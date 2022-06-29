package com.arkivanov.mvikotlin.sample.reaktive.app

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.database.DefaultTodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.timetravel.TimeTravelServer
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import com.badoo.reaktive.subject.publish.PublishSubject
import csstype.AlignItems
import csstype.Display
import csstype.JustifyContent
import csstype.px
import csstype.vh
import csstype.vw
import kotlinx.browser.document
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
    TimeTravelServer().start()

    createRoot(document.getElementById("app")!!).render(
        Root.create {
            storeFactory = LoggingStoreFactory(delegate = TimeTravelStoreFactory())
            database = DefaultTodoDatabase()
        }
    )
}

external interface RootProps : Props {
    var storeFactory: StoreFactory
    var database: TodoDatabase
}

val Root: FC<RootProps> = FC { props ->
    val mainInput = useMemo { PublishSubject<MainInput>() }

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
                                itemId = requireNotNull(params["itemId"])
                                onFinished = { navigate(delta = -1) }
                                onItemChanged = { id, data -> mainInput.onNext(MainInput.ItemChanged(id = id, data = data)) }
                                onItemDeleted = { id ->
                                    mainInput.onNext(MainInput.ItemDeleted(id = id))
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
