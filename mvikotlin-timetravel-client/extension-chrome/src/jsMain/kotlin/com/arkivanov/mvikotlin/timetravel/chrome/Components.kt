package com.arkivanov.mvikotlin.timetravel.chrome

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun ImageButton(
    iconName: String,
    title: String,
    isEnabled: Boolean = true,
    attrs: AttrBuilderContext<*> = {},
    onClick: () -> Unit,
) {
    A(
        attrs = {
            classes()
            classesOfNotNull("waves-effect", "waves-teal", "btn-flat", if (isEnabled) null else "disabled")
            style {
                width(48.px)
                height(48.px)
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.Center)
            }
            title(title)
            this.onClick { onClick() }
            attrs()
        }
    ) {
        MaterialIcon(name = iconName)
    }
}

@Composable
internal fun MaterialIcon(name: String) {
    I(attrs = { classes("material-icons") }) { Text(value = name) }
}
