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
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.graphics.alpha
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.mm2d_cc_view_control.view.*
import net.mm2d.color.chooser.util.AttrUtils
import net.mm2d.color.chooser.util.setAlpha
import net.mm2d.color.chooser.util.toOpacity

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class ControlView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ColorChangeObserver {
    private var changeHexTextByUser = true
    private val normalTint =
        ColorStateList.valueOf(AttrUtils.resolveColor(context, R.attr.colorAccent, Color.BLUE))
    private val errorTint =
        ColorStateList.valueOf(AttrUtils.resolveColor(context, R.attr.colorError, Color.RED))
    var observer: ColorChangeObserver? = null
    var color: Int = Color.BLACK
        private set
    var hasAlpha: Boolean = true
        set(hasAlpha) {
            field = hasAlpha
            section_alpha.isVisible = hasAlpha
            if (hasAlpha){
                edit_hex.filters = argbFilter
            } else {
                edit_hex.filters = rgbFilter
                alpha = 0xff
            }
        }
    var alpha: Int
        get() = seek_alpha.value
        set(alpha) {
            seek_alpha.value = alpha
            color = color.setAlpha(alpha)
            color_preview.color = color
            setColorToHexText()
        }
    private val rgbFilter: Array<InputFilter>
    private val argbFilter: Array<InputFilter>

    init {
        orientation = VERTICAL
        inflate(context, R.layout.mm2d_cc_view_control, this)
        color_preview.color = color
        seek_alpha.value = color.alpha
        seek_alpha.onValueChanged = { value, fromUser ->
            text_alpha.text = value.toString()
            if (fromUser) {
                alpha = value
            }
        }
        val hexFilter = InputFilter { source, _, _, _, _, _ ->
            source.toString().toUpperCase().replace("[^0-9A-F]".toRegex(), "")
        }
        rgbFilter = arrayOf(hexFilter, LengthFilter(6))
        argbFilter = arrayOf(hexFilter, LengthFilter(8))
        edit_hex.filters = argbFilter
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
                    color_preview.color = color
                    seek_alpha.value = color.alpha
                    observer?.onChange(color.toOpacity(), this@ControlView)
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
        this.color = color.setAlpha(seek_alpha.value)
        color_preview.color = this.color
        setColorToHexText()
        seek_alpha.maxColor = color
    }

    @SuppressLint("SetTextI18n")
    private fun setColorToHexText() {
        changeHexTextByUser = false
        if (hasAlpha) {
            edit_hex.setText("%08X".format(color))
        } else {
            edit_hex.setText("%06X".format(color and 0xffffff))
        }
        clearError()
        changeHexTextByUser = true
    }
}
