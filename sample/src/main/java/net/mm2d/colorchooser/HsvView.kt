/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.colorchooser

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_hsv.view.*

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class HsvView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var color: Int = Color.BLACK
    var onColorChanged: ((color: Int) -> Unit)? = null

    init {
        orientation = HORIZONTAL
        inflate(context, R.layout.view_hsv, this)
        sv_view.onColorChanged = {
            color = it
            onColorChanged?.invoke(color)
        }
        hue_view.onHueChanged = {
            color = ColorUtils.hsvToColor(it, sv_view.saturation, sv_view.value)
            sv_view.setColor(color)
            onColorChanged?.invoke(color)
        }
    }

    fun setColor(color: Int) {
        this.color = color
        sv_view.setColor(color)
        hue_view.setColor(color)
    }
}
