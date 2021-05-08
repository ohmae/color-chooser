/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.use
import androidx.viewpager.widget.ViewPager

internal class MaxHeightViewPager
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {
    private val maxHeight: Int =
        context.obtainStyledAttributes(attrs, R.styleable.MaxHeightViewPager).use {
            it.getDimensionPixelSize(R.styleable.SwipeDisabledViewPager_maxHeight, 0)
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val size = MeasureSpec.getSize(heightMeasureSpec)
        val heightSpec = if (mode != MeasureSpec.EXACTLY && maxHeight in 1..size)
            MeasureSpec.makeMeasureSpec(maxHeight, mode) else heightMeasureSpec
        super.onMeasure(widthMeasureSpec, heightSpec)
    }
}
