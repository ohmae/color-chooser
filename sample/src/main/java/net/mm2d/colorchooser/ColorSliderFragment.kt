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
import android.widget.SeekBar
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
        seek_red.setOnSeekBarChangeListener(makeOnSeekBarChangeListener { progress, fromUser ->
            text_red.text = progress.toString()
            updateBySeek(fromUser)
        })
        seek_green.setOnSeekBarChangeListener(makeOnSeekBarChangeListener { progress, fromUser ->
            text_green.text = progress.toString()
            updateBySeek(fromUser)
        })
        seek_blue.setOnSeekBarChangeListener(makeOnSeekBarChangeListener { progress, fromUser ->
            text_blue.text = progress.toString()
            updateBySeek(fromUser)
        })
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
                    setColorToSeekBar()
                    setColorToPreview()
                } catch (e: IllegalArgumentException) {
                    edit_hex_layout.error = "error"
                }
            }
        })
        sv_view.onChangeSv = { saturation, value ->
            color = ColorUtils.hsvToColor(hue_view.hue, saturation, value)
            setColorToHexText()
            setColorToSeekBar()
            setColorToPreview()
        }
        hue_view.onChangeHue = { hue ->
            color = ColorUtils.hsvToColor(hue, sv_view.saturation, sv_view.value)
            setColorToHexText()
            setColorToSeekBar()
            setColorToPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        setColorToHexText()
        setColorToSeekBar()
        setColorToPreview()
    }

    private fun updateBySeek(fromUser: Boolean) {
        if (fromUser) {
            color = Color.rgb(seek_red.progress, seek_green.progress, seek_blue.progress)
            setColorToHexText()
            setColorToPreview()
        }
    }

    private fun setColorToPreview() {
        color_preview.setBackgroundColor(color)
        sv_view.setColor(color)
        hue_view.setColor(color)
    }

    private fun setColorToSeekBar() {
        seek_red.progress = Color.red(color)
        seek_green.progress = Color.green(color)
        seek_blue.progress = Color.blue(color)
    }

    @SuppressLint("SetTextI18n")
    private fun setColorToHexText() {
        changeHexTextByUser = false
        edit_hex.setText("#%06X".format(color and 0xFFFFFF))
        changeHexTextByUser = true
    }

    private fun makeOnSeekBarChangeListener(onProgressChanged: (Int, Boolean) -> Unit): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                onProgressChanged(progress, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        }
    }
}