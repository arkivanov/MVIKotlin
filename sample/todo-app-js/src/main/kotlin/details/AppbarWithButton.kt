package details

import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.mToolbar
import com.ccfraser.muirwik.components.spacingUnits
import com.ccfraser.muirwik.components.themeContext
import kotlinx.css.*
import react.RBuilder
import styled.css

fun RBuilder.appBarWithButton(icon: String, onIconClick: () -> Unit) {
    themeContext.Consumer { theme ->
        mToolbar {
            css {
                zIndex = theme.zIndex.drawer + 1
                position = Position.absolute
                width = 100.pct
                alignContent = Align.flexEnd
            }

            mIconButton(icon, onClick = { onIconClick() }) {
                css {
                    position = Position.absolute
                    right = 1.spacingUnits
                }
            }
        }
    }
}
