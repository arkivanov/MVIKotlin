package timetravel

import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.list.*
import com.ccfraser.muirwik.components.mTypography
import com.ccfraser.muirwik.components.themeContext
import kotlinx.css.Color
import kotlinx.css.backgroundColor
import kotlinx.css.px
import kotlinx.css.width
import react.RBuilder
import react.dom.span
import styled.css

val altBuilder = RBuilder()

fun RBuilder.timeTravelEventsView(
    events: List<TimeTravelEvent>,
    selectedEventIndex: Int,
    onDebugEventClick: (Long) -> Unit,
    onItemClick: (TimeTravelEvent) -> Unit
) {
    themeContext.Consumer { theme ->
        mList {
            css {
                width = 320.px
                backgroundColor = Color(theme.palette.background.paper)
            }
            events.forEach { event ->
                mListItem(
                    alignItems = MListItemAlignItems.flexStart,
                    onClick = { onItemClick(event) }
                ) {
                    mListItemText(
                        primary = altBuilder.span { +event.storeName },
                        secondary = altBuilder.span {
                            mTypography(
                                text = event.value.toString(),
                                component = "span",
                                variant = MTypographyVariant.body2
                            )
                        }
                    )
                    if (event.type !== StoreEventType.STATE)
                        mListItemSecondaryAction {
                            mIconButton(
                                iconName = "play_arrow",
                                onClick = { onDebugEventClick(event.id) }
                            )
                        }
                }
            }
        }
    }
}
