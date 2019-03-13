/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.graphics.alpha
import kotlinx.android.synthetic.main.mm2d_cc_view_dialog.view.*
import net.mm2d.color.chooser.util.toOpacity

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class DialogView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ColorChangeObserver {
    private val observers: List<ColorChangeObserver>
    val color: Int
        get() = control_view.color

    init {
        orientation = VERTICAL
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.mm2d_cc_view_dialog, this)
        val pages: List<Pair<String, View>> = listOf(
            "palette" to PaletteView(context).also { it.observer = this },
            "hsv" to HsvPage(context).also { it.observer = this },
            "rgb" to SliderPage(context).also { it.observer = this }
        )
        view_pager.adapter = ViewPagerAdapter(pages)
        tab_layout.setupWithViewPager(view_pager)
        control_view.observer = this
        observers = pages.map { it.second as ColorChangeObserver }
            .plus(control_view)
    }

    override fun onChange(color: Int, notifier: Any?) {
        observers.forEach { it.onChange(color, notifier) }
    }

    fun setWithAlpha(withAlpha: Boolean) {
        control_view.setWithAlpha(withAlpha)
    }

    fun setColor(color: Int) {
        onChange(color.toOpacity(), null)
        control_view.setAlpha(color.alpha)
    }
}
