/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.colorchooser

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class HueView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    @ColorInt
    private var color: Int = Color.BLACK
    private val paint = Paint()
    private val bitmap: Bitmap
    private val _padding = resources.getDimensionPixelOffset(R.dimen.panel_margin)
    private val _width = resources.getDimensionPixelOffset(R.dimen.hue_width) + _padding * 2
    private val _height = resources.getDimensionPixelOffset(R.dimen.hsv_size) + _padding * 2
    private val _sampleRadius = resources.getDimension(R.dimen.sample_radius)
    private val _sampleFrameRadius = _sampleRadius + resources.getDimension(R.dimen.sample_frame)
    private val _sampleShadowRadius =
        _sampleFrameRadius + resources.getDimension(R.dimen.sample_shadow)
    private val bitmapRect = Rect(0, 0, 1, RANGE)
    private val targetRect = Rect()
    private var hue: Float = 0f
    var onHueChanged: ((hue: Float) -> Unit)? = null
    private val colorSampleFrame = ContextCompat.getColor(context, R.color.sample_frame)
    private val colorSampleShadow = ContextCompat.getColor(context, R.color.sample_shadow)

    init {
        val pixels = IntArray(RANGE) {
            ColorUtils.hsvToColor(it.toFloat() / RANGE, 1f, 1f)
        }
        bitmap = Bitmap.createBitmap(pixels, 0, 1, 1, RANGE, Bitmap.Config.ARGB_8888)
        color = ColorUtils.hsvToColor(hue, 1f, 1f)
    }

    fun setColor(@ColorInt color: Int) {
        updateHue(ColorUtils.hue(color))
    }

    private fun updateHue(h: Float, fromUser: Boolean = false) {
        if (hue == h) {
            return
        }
        hue = h
        color = ColorUtils.hsvToColor(hue, 1f, 1f)
        invalidate()
        if (fromUser) {
            onHueChanged?.invoke(hue)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        updateHue(((event.y - targetRect.top) / targetRect.height()).clamp(0f, 1f), true)
        return true
    }

    override fun onDraw(canvas: Canvas) {
        targetRect.set(
            paddingLeft + _padding,
            paddingTop + _padding,
            width - paddingRight - _padding,
            height - paddingBottom - _padding
        )
        canvas.drawBitmap(bitmap, bitmapRect, targetRect, paint)
        val x = targetRect.centerX().toFloat()
        val y = hue * targetRect.height() + targetRect.top
        paint.color = colorSampleShadow
        canvas.drawCircle(x, y, _sampleShadowRadius, paint)
        paint.color = colorSampleFrame
        canvas.drawCircle(x, y, _sampleFrameRadius, paint)
        paint.color = color
        canvas.drawCircle(x, y, _sampleRadius, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            resolveSizeAndState(
                Math.max(_width + paddingLeft + paddingRight, suggestedMinimumWidth),
                widthMeasureSpec,
                MeasureSpec.UNSPECIFIED
            ),
            resolveSizeAndState(
                Math.max(_height + paddingTop + paddingBottom, suggestedMinimumHeight),
                heightMeasureSpec,
                MeasureSpec.UNSPECIFIED
            )
        )
    }

    companion object {
        private const val RANGE = 360
    }
}
