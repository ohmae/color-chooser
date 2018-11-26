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
    private val _width: Int
    private val _height: Int
    private val _padding: Int
    private val _sampleRadius: Float
    private val _sampleFrameRadius: Float
    private val _sampleShadowRadius: Float
    private val bitmapRect = Rect(0, 0, 1, RANGE)
    private val targetRect = Rect()
    var hue: Float = 0f
        private set
    var onChangeHue: ((hue: Float) -> Unit)? = null

    init {
        val density = resources.displayMetrics.density
        _padding = (PADDING * density + 0.5f).toInt()
        _width = (WIDTH * density + 0.5f).toInt() + _padding * 2
        _height = (HEIGHT * density + 0.5f).toInt() + _padding * 2
        _sampleRadius = SAMPLE_RADIUS * density
        _sampleFrameRadius = SAMPLE_FRAME_RADIUS * density
        _sampleShadowRadius = SAMPLE_SHADOW_RADIUS * density
        val pixels = IntArray(RANGE) {
            ColorUtils.hsvToColor(it.toFloat() / RANGE, 1f, 1f)
        }
        bitmap = Bitmap.createBitmap(pixels, 0, 1, 1, RANGE, Bitmap.Config.ARGB_8888)
        color = ColorUtils.hsvToColor(hue, 1f, 1f)
    }

    fun setColor(@ColorInt color: Int) {
        updateHue(ColorUtils.hue(color))
    }

    private fun updateHue(h: Float, byUser: Boolean = false) {
        if (hue == h) {
            return
        }
        hue = h
        color = ColorUtils.hsvToColor(hue, 1f, 1f)
        invalidate()
        if (byUser) {
            onChangeHue?.invoke(hue)
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
        paint.color = SAMPLE_SHADOW_COLOR
        canvas.drawCircle(x, y, _sampleShadowRadius, paint)
        paint.color = SAMPLE_FRAME_COLOR
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
        private const val WIDTH = 24
        private const val HEIGHT = 256
        private const val PADDING = 8
        private const val SAMPLE_RADIUS = 5
        private const val SAMPLE_FRAME_RADIUS = 7
        private const val SAMPLE_SHADOW_RADIUS = 8
        private const val SAMPLE_FRAME_COLOR = Color.WHITE
        private const val SAMPLE_SHADOW_COLOR = 0x1a000000
    }
}
