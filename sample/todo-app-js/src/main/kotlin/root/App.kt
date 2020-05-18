package root
import com.ccfraser.muirwik.components.mCssBaseline
import com.ccfraser.muirwik.components.mThemeProvider
import com.ccfraser.muirwik.components.styles.ThemeOptions
import com.ccfraser.muirwik.components.styles.createMuiTheme
import list.todoContainer
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

abstract class App : RComponent<RProps, AppState>() {

    private var themeColor = "light"

    init {
        state = AppState()
    }

    override fun RBuilder.render() {
        mCssBaseline()
        @Suppress("UnsafeCastFromDynamic")
        val themeOptions: ThemeOptions = js("({palette: { type: 'placeholder', primary: {main: 'placeholder'}}})")
        themeOptions.palette?.type = themeColor
        themeOptions.palette?.primary.main = "#e91e63"
        themeOptions.spacing = 1
        mThemeProvider(createMuiTheme(themeOptions)) {
            todoContainer()
        }

    }
}

class AppState : RState

fun RBuilder.app() = child(App::class) {}