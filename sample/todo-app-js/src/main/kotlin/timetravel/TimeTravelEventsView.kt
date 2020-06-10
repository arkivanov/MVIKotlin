package timetravel

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.list.*
import com.ccfraser.muirwik.components.mTypography
import org.w3c.dom.Element
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.findDOMNode
import react.dom.span
import root.App.TodoStyles.eventItemCss
import styled.css

class TimeTravelEventsView(prps: TimeTravelEventsProps) :
    RComponent<TimeTravelEventsProps, TimeTravelEventsState>(prps) {

    private var buttonRef: Element? = null

    override fun componentDidUpdate(prevProps: TimeTravelEventsProps, prevState: TimeTravelEventsState, snapshot: Any) {
        if (buttonRef != null) {
            buttonRef!!.scrollIntoView()
        }
    }

    override fun RBuilder.render() {
        val altBuilder = RBuilder()
        mList {
            props.events.forEachIndexed { index, event ->
                val selected = props.selectedEventIndex == index
                mListItem(
                    alignItems = MListItemAlignItems.flexStart,
                    onClick = { props.onItemClick(event) },
                    selected = selected
                ) {
                    if (selected) {
                        ref {
                            buttonRef = findDOMNode(it)
                        }
                    }
                    mListItemText(
                        primary = altBuilder.span { +event.storeName },
                        secondary = altBuilder.span {
                            mTypography(
                                text = event.value.toString(),
                                component = "span",
                                variant = MTypographyVariant.body2
                            ) { css(eventItemCss) }
                        }
                    )
                    if (event.type !== StoreEventType.STATE)
                        mListItemSecondaryAction {
                            mIconButton(
                                iconName = "play_arrow",
                                onClick = { props.onDebugEventClick(event.id) }
                            )
                        }
                }
            }
        }
    }
}

interface TimeTravelEventsProps : RProps {
    var events: List<TimeTravelEvent>
    var selectedEventIndex: Int
    var onDebugEventClick: (Long) -> Unit
    var onItemClick: (TimeTravelEvent) -> Unit
}

class TimeTravelEventsState : RState

fun RBuilder.timeTravelEventsView(
    events: List<TimeTravelEvent>,
    selectedEventIndex: Int,
    onDebugEventClick: (Long) -> Unit,
    onItemClick: (TimeTravelEvent) -> Unit
) = child(TimeTravelEventsView::class) {
    attrs.events = events
    attrs.selectedEventIndex = selectedEventIndex
    attrs.onDebugEventClick = onDebugEventClick
    attrs.onItemClick = onItemClick
}
