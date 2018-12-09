/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.page_slider.view.*

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class SliderPage
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ColorChangeObserver {
    var observer: ColorChangeObserver? = null

    init {
        inflate(context, R.layout.page_slider, this)
        slider_view.observer = object : ColorChangeObserver {
            override fun onChange(color: Int, notifier: Any?) {
                observer?.onChange(color, this@SliderPage)
            }
        }
    }

    override fun onChange(color: Int, notifier: Any?) {
        if (notifier == this) return
        slider_view.onChange(color, notifier)
    }
}
