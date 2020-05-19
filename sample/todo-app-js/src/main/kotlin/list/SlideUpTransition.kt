package list

import ANIMATION_DURATION
import com.ccfraser.muirwik.components.transitions.MTransitionProps
import com.ccfraser.muirwik.components.transitions.SlideTransitionDirection
import com.ccfraser.muirwik.components.transitions.mSlide
import react.RBuilder
import react.RComponent
import react.RState
import react.cloneElement

class SlideUpTransitionComponent(
    props: MTransitionProps
) : RComponent<MTransitionProps, RState>(props) {
    override fun RBuilder.render() {
        childList.add(
            cloneElement(
                element = mSlide(
                    direction = SlideTransitionDirection.down,
                    addAsChild = false
                ) {
                    attrs.asDynamic().timeout = ANIMATION_DURATION
                },
                props = props
            )
        )
    }
}
