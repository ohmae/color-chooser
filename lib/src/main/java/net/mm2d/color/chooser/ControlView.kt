/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.view_control.view.*

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class ControlView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ColorChangeObserver {
    private var color: Int = Color.BLACK
    private val background: GradientDrawable
    private var changeHexTextByUser = true
    var observer: ColorChangeObserver? = null

    init {
        orientation = HORIZONTAL
        inflate(context, R.layout.view_control, this)
        background = initDrawable(context, color_preview)
        background.setColor(color)

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
                    return
                }
                try {
                    color = Color.parseColor(s.toString())
                    edit_hex_layout.error = null
                    background.setColor(color)
                    observer?.onChange(color, this@ControlView)
                } catch (e: IllegalArgumentException) {
                    edit_hex_layout.error = "error"
                }
            }
        })
    }

    override fun onChange(color: Int, notifier: Any?) {
        if (notifier == this) return
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

    companion object {
        fun initDrawable(context: Context, view: View): GradientDrawable {
            val resources = context.resources
            val frameWidth = resources.getDimensionPixelSize(R.dimen.sample_frame)
            val shadowWidth = resources.getDimensionPixelSize(R.dimen.sample_shadow)
            val background = GradientDrawable().also {
                it.shape = GradientDrawable.RECTANGLE
                it.setStroke(frameWidth, ContextCompat.getColor(context, R.color.sample_frame))
            }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                view.background = background
                return background
            }
            val shadow = GradientDrawable().also {
                it.shape = GradientDrawable.RECTANGLE
                it.setStroke(shadowWidth, ContextCompat.getColor(context, R.color.sample_shadow))
            }
            val layerDrawable = LayerDrawable(arrayOf(background, shadow))
            layerDrawable.setLayerInset(0, shadowWidth, shadowWidth, shadowWidth, shadowWidth)
            view.background = layerDrawable
            return background
        }
    }
}