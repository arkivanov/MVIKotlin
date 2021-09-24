package timetravel

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.list.MListItemAlignItems
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemSecondaryAction
import com.ccfraser.muirwik.components.list.mListItemText
import com.ccfraser.muirwik.components.mTypography
import org.w3c.dom.Element
import react.Props
import react.RBuilder
import react.RComponent
import react.RefCallback
import react.State
import react.buildElement
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
        mList {
            props.events.forEachIndexed { index, event ->
                val selected = props.selectedEventIndex == index
                mListItem(
                    alignItems = MListItemAlignItems.flexStart,
                    onClick = { props.onItemClick(event) },
                    selected = selected
                ) {
                    if (selected) {
                        ref = RefCallback<dynamic> { buttonRef = findDOMNode(it) }
                    }
                    mListItemText(
                        primary = buildElement { span { +event.storeName } },
                        secondary = buildElement {
                            span {
                                mTypography(
                                    text = event.value.toString(),
                                    component = "span",
                                    variant = MTypographyVariant.body2
                                ) { css(eventItemCss) }
                            }
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

external interface TimeTravelEventsProps : Props {
    var events: List<TimeTravelEvent>
    var selectedEventIndex: Int
    var onDebugEventClick: (Long) -> Unit
    var onItemClick: (TimeTravelEvent) -> Unit
}

external interface TimeTravelEventsState : State

fun RBuilder.timeTravelEventsView(
    events: List<TimeTravelEvent>,
    selectedEventIndex: Int,
    onDebugEventClick: (Long) -> Unit,
    onItemClick: (TimeTravelEvent) -> Unit
) {
    child(TimeTravelEventsView::class) {
        attrs.events = events
        attrs.selectedEventIndex = selectedEventIndex
        attrs.onDebugEventClick = onDebugEventClick
        attrs.onItemClick = onItemClick
    }
}
