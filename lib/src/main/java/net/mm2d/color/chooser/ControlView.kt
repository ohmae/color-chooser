/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.mm2d_cc_view_control.view.*
import net.mm2d.color.chooser.util.AttrUtils

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
    private val normalTint =
        ColorStateList.valueOf(AttrUtils.resolveColor(context, R.attr.colorAccent, Color.BLUE))
    private val errorTint =
        ColorStateList.valueOf(AttrUtils.resolveColor(context, R.attr.colorError, Color.RED))
    var observer: ColorChangeObserver? = null

    init {
        orientation = HORIZONTAL
        inflate(context, R.layout.mm2d_cc_view_control, this)
        background = initDrawable(context, color_preview)
        background.setColor(color)
        edit_hex.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source.toString().toUpperCase().replace("[^0-9A-F]".toRegex(), "")
        }, LengthFilter(6))
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
                    setError()
                    return
                }
                try {
                    color = Color.parseColor("#$s")
                    clearError()
                    background.setColor(color)
                    observer?.onChange(color, this@ControlView)
                } catch (e: IllegalArgumentException) {
                    setError()
                }
            }
        })
    }

    private fun setError() {
        ViewCompat.setBackgroundTintList(edit_hex, errorTint)
    }

    private fun clearError() {
        ViewCompat.setBackgroundTintList(edit_hex, normalTint)
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
        edit_hex.setText("%06X".format(color and 0xFFFFFF))
        clearError()
        changeHexTextByUser = true
    }

    companion object {
        fun initDrawable(context: Context, view: View): GradientDrawable {
            val resources = context.resources
            val frameWidth = resources.getDimensionPixelSize(R.dimen.mm2d_cc_sample_frame)
            val shadowWidth = resources.getDimensionPixelSize(R.dimen.mm2d_cc_sample_shadow)
            val background = GradientDrawable().also {
                it.shape = GradientDrawable.RECTANGLE
                it.setStroke(frameWidth, ContextCompat.getColor(context, R.color.mm2d_cc_sample_frame))
            }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                view.background = background
                return background
            }
            val shadow = GradientDrawable().also {
                it.shape = GradientDrawable.RECTANGLE
                it.setStroke(shadowWidth, ContextCompat.getColor(context, R.color.mm2d_cc_sample_shadow))
            }
            val layerDrawable = LayerDrawable(arrayOf(background, shadow))
            layerDrawable.setLayerInset(0, shadowWidth, shadowWidth, shadowWidth, shadowWidth)
            view.background = layerDrawable
            return background
        }
    }
}
