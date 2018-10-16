package com.arkivanov.mvidroid.widget

import android.content.Context
import android.support.v4.widget.DrawerLayout
import android.util.AttributeSet
import android.view.Gravity
import android.view.View

/**
 * Handy drawer that displays [MviTimeTravelView].
 * Set your content using [MviTimeTravelDrawer.setContentView] or specify it as a single child in XML.
 */
class MviTimeTravelDrawer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : DrawerLayout(context, attrs, defStyle) {

    init {
        addView(MviTimeTravelView(context), LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.END))
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        getContentViewIndex()
            ?.takeIf { it > 0 }
            ?.also {
                val view = getChildAt(it)
                removeViewAt(it)
                addView(view, 0)
            }
    }

    /**
     * Sets the provided view as content, replacing any existing content view
     *
     * @param view content view
     */
    fun setContentView(view: View) {
        getContentViewIndex()?.also(::removeViewAt)
        addView(view, 0)
    }

    private fun getContentViewIndex(): Int? {
        for (i in 0 until childCount) {
            if (((getChildAt(i).layoutParams as? DrawerLayout.LayoutParams)?.gravity ?: Gravity.NO_GRAVITY) == Gravity.NO_GRAVITY) {
                return i
            }
        }

        return null
    }
}