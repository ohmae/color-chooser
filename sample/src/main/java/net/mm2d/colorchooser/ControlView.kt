/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.colorchooser

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_control.view.*

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class ControlView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var color: Int = Color.BLACK
    private val background: GradientDrawable = GradientDrawable()
    private var changeHexTextByUser = true
    var onChangeColor: ((color: Int) -> Unit)? = null

    init {
        orientation = HORIZONTAL
        inflate(context, R.layout.view_control, this)

        val density = resources.displayMetrics.density
        background.shape = GradientDrawable.RECTANGLE
        background.setColor(color)
        val frame = GradientDrawable()
        frame.shape = GradientDrawable.RECTANGLE
        frame.setStroke((2 * density + 0.5f).toInt(), Color.WHITE)
        val shadow = GradientDrawable()
        shadow.shape = GradientDrawable.RECTANGLE
        shadow.setStroke((1 * density + 0.5f).toInt(), Color.argb(0x1a, 0, 0, 0))
        val layer = LayerDrawable(arrayOf(background, frame, shadow))
        layer.setLayerInset(0, (2 * density).toInt(), (2 * density).toInt(), (2 * density).toInt(), (2 * density).toInt())
        layer.setLayerInset(1, (1 * density).toInt(), (1 * density).toInt(), (1 * density).toInt(), (1 * density).toInt())
        color_preview.background = layer

        edit_hex.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!changeHexTextByUser) {
                    return
                }
                if (s.isNullOrEmpty()) {
                    edit_hex_layout.error = "error"
                }
                try {
                    color = Color.parseColor(s.toString())
                    edit_hex_layout.error = null
                    background.setColor(color)
                    onChangeColor?.invoke(color)
                } catch (e: IllegalArgumentException) {
                    edit_hex_layout.error = "error"
                }
            }
        })
    }

    fun setColor(color: Int) {
        this.color = color
        background.setColor(color)
        setColorToHexText()
    }

    @SuppressLint("SetTextI18n")
    private fun setColorToHexText() {
        changeHexTextByUser = false
        edit_hex.setText("#%06X".format(color and 0xFFFFFF))
        changeHexTextByUser = true
    }
}
