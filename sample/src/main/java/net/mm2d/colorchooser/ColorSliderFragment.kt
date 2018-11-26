/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.colorchooser

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_color_slider.*

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class ColorSliderFragment : Fragment() {
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
            setColorToControl()
            setColorToHsv()
        }
        hsv_view.onChangeColor = {
            color = it
            setColorToSlider()
            setColorToControl()
        }
        control_view.onChangeColor = {
            color = it
            setColorToSlider()
            setColorToHsv()
        }
    }

    override fun onResume() {
        super.onResume()
        setColorToSlider()
        setColorToHsv()
        setColorToControl()
    }

    private fun setColorToControl() {
        control_view.setColor(color)
    }

    private fun setColorToSlider() {
        slider_view.setColor(color)
    }

    private fun setColorToHsv() {
        hsv_view.setColor(color)
    }
}