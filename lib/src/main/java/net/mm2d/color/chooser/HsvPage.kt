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
import kotlinx.android.synthetic.main.page_hsv.view.*

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class HsvPage
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ColorChangeObserver {
    var observer: ColorChangeObserver? = null

    init {
        inflate(context, R.layout.page_hsv, this)
        hsv_view.observer = object : ColorChangeObserver {
            override fun onChange(color: Int, notifier: Any?) {
                observer?.onChange(color, this@HsvPage)
            }
        }
    }

    override fun onChange(color: Int, notifier: Any?) {
        if (notifier == this) return
        hsv_view.onChange(color, notifier)
    }
}
