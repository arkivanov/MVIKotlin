package list

import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.button.MButtonSize
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.mTextField
import com.ccfraser.muirwik.components.spacingUnits
import com.ccfraser.muirwik.components.targetInputValue
import kotlinx.css.margin
import react.RBuilder
import styled.css

fun RBuilder.addTodo(
    textValue: String,
    onTextChanged: (String) -> Unit,
    onAddClick: () -> Unit
) {
    mTextField(
        label = "Write ToDo",
        value = textValue,
        onChange = { event -> onTextChanged(event.targetInputValue) },
        fullWidth = true
    ) { css { margin(1.spacingUnits) } }
    mButton(
        caption = "Add",
        variant = MButtonVariant.outlined,
        onClick = { onAddClick() },
        size = MButtonSize.small,
        color = MColor.primary
    ) { css { margin(1.spacingUnits) } }
}
