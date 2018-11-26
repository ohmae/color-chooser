/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.colorchooser

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_color_slider.*

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class ColorSliderFragment : Fragment() {
    private var changeHexTextByUser = true
    private var color: Int = Color.BLACK

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_color_slider, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        slider_view.onChangeColor = {
            color = it
            setColorToHexText()
            setColorToPreview()
            setColorToHsv()
        }
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
                    setColorToSlider()
                    setColorToHsv()
                    setColorToPreview()
                } catch (e: IllegalArgumentException) {
                    edit_hex_layout.error = "error"
                }
            }
        })
        hsv_view.onChangeColor = {
            color = it
            setColorToSlider()
            setColorToHexText()
            setColorToPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        setColorToSlider()
        setColorToHsv()
        setColorToHexText()
        setColorToPreview()
    }

    private fun setColorToPreview() {
        color_preview.setBackgroundColor(color)
    }

    private fun setColorToSlider() {
        slider_view.setColor(color)
    }

    private fun setColorToHsv() {
        hsv_view.setColor(color)
    }

    @SuppressLint("SetTextI18n")
    private fun setColorToHexText() {
        changeHexTextByUser = false
        edit_hex.setText("#%06X".format(color and 0xFFFFFF))
        changeHexTextByUser = true
    }
}