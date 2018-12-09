/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class SwipeDisabledViewPager
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {
    private val maxHeight: Int
    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SwipeDisabledViewPager)
        maxHeight = a.getDimensionPixelSize(R.styleable.SwipeDisabledViewPager_maxHeight, 0)
        a.recycle()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val size = MeasureSpec.getSize(heightMeasureSpec)
        if (mode != MeasureSpec.EXACTLY && maxHeight in 1..size) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(maxHeight, mode))
            return
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
