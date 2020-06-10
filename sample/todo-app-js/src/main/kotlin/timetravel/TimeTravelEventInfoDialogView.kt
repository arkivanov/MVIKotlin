package timetravel

import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogActions
import com.ccfraser.muirwik.components.dialog.mDialogContent
import com.ccfraser.muirwik.components.dialog.mDialogTitle
import com.ccfraser.muirwik.components.mTypography
import react.RBuilder

fun RBuilder.infoDialog(open: Boolean, event: TimeTravelEvent, onClose: () -> Unit) {
    mDialog(open = open, onClose = { _, _ -> onClose() }) {
        mDialogTitle(text = event.storeName)
        mDialogContent {
            mTypography(
                text = event.value.toString(),
                component = "span",
                variant = MTypographyVariant.body2
            )
        }
        mDialogActions {
            mButton("Close", color = MColor.primary, onClick = { onClose() })
        }
    }
}
