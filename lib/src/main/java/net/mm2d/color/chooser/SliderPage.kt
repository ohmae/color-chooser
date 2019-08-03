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
import kotlinx.android.synthetic.main.mm2d_cc_page_slider.view.*
import net.mm2d.color.chooser.util.toOpacity

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
internal class SliderPage
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ColorChangeObserver {
    var observer: ColorChangeObserver? = null

    init {
        inflate(context, R.layout.mm2d_cc_page_slider, this)
        slider_view.observer = colorChangeObserver { color, _ ->
            observer?.onChange(color, this)
        }
    }

    override fun onChange(color: Int, notifier: Any?) {
        if (notifier == this) return
        slider_view.onChange(color.toOpacity(), notifier)
    }
}
