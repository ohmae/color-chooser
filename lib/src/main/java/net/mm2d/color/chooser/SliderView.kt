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
import android.widget.SeekBar
import kotlinx.android.synthetic.main.mm2d_cc_view_slider.view.*

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class SliderView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ColorChangeObserver {
    var observer: ColorChangeObserver? = null

    init {
        orientation = VERTICAL
        inflate(context, R.layout.mm2d_cc_view_slider, this)
        seek_red.setOnSeekBarChangeListener(makeOnSeekBarChangeListener { progress, fromUser ->
            text_red.text = progress.toString()
            updateBySeekBar(fromUser)
        })
        seek_green.setOnSeekBarChangeListener(makeOnSeekBarChangeListener { progress, fromUser ->
            text_green.text = progress.toString()
            updateBySeekBar(fromUser)
        })
        seek_blue.setOnSeekBarChangeListener(makeOnSeekBarChangeListener { progress, fromUser ->
            text_blue.text = progress.toString()
            updateBySeekBar(fromUser)
        })
    }

    override fun onChange(color: Int, notifier: Any?) {
        if (notifier == this) return
        seek_red.progress = Color.red(color)
        seek_green.progress = Color.green(color)
        seek_blue.progress = Color.blue(color)
    }

    private fun updateBySeekBar(fromUser: Boolean) {
        if (!fromUser) return
        val color = Color.rgb(seek_red.progress, seek_green.progress, seek_blue.progress)
        observer?.onChange(color, this)
    }

    companion object {
        private fun makeOnSeekBarChangeListener(onProgressChanged: (progress: Int, fromUser: Boolean) -> Unit): SeekBar.OnSeekBarChangeListener {
            return object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    onProgressChanged(progress, fromUser)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        }
    }
}
