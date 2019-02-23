/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.mm2d_cc_view_hsv.view.*
import net.mm2d.color.chooser.util.ColorUtils

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class HsvView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ColorChangeObserver {
    private var color: Int = Color.BLACK
    var observer: ColorChangeObserver? = null

    init {
        orientation = HORIZONTAL
        inflate(context, R.layout.mm2d_cc_view_hsv, this)
        sv_view.onColorChanged = {
            color = it
            observer?.onChange(color, this)
        }
        hue_view.onHueChanged = {
            color = ColorUtils.hsvToColor(it, sv_view.saturation, sv_view.value)
            sv_view.setHue(it)
            observer?.onChange(color, this)
        }
    }

    override fun onChange(color: Int, notifier: Any?) {
        if (notifier == this) return
        this.color = color
        sv_view.setColor(color)
        hue_view.setColor(color)
    }
}
