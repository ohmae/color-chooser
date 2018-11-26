/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.colorchooser

import android.content.Context
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
class ControlFragment : Fragment(), ColorChangeObserver {
    private var color = Color.BLACK
    private var colorChangeObserver: ColorChangeObserver? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_color_slider, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        colorChangeObserver = context as? ColorChangeObserver
    }

    override fun onDetach() {
        super.onDetach()
        colorChangeObserver = null
    }

    override fun onColorChange(color: Int, fragment: Fragment?) {
        if (fragment is ControlFragment) return
        this.color = color
        setColor(color)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        control_view.onColorChanged = {
            color = it
            performOnColorChange(it)
        }
    }

    override fun onResume() {
        super.onResume()
        setColor(color)
    }

    private fun performOnColorChange(color: Int) {
        colorChangeObserver?.onColorChange(color, this)
    }

    private fun setColor(color: Int) {
        control_view?.setColor(color)
    }
}
