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
class SvView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    @ColorInt
    private var color: Int = Color.BLACK
    private val bitmap: Bitmap = Bitmap.createBitmap(TONE_SIZE, TONE_SIZE, Bitmap.Config.ARGB_8888)
    private val pixels = IntArray(TONE_SIZE * TONE_SIZE)
    private val paint = Paint()
    private val _padding = resources.getDimensionPixelOffset(R.dimen.panel_margin)
    private val _width = resources.getDimensionPixelOffset(R.dimen.hsv_size) + _padding * 2
    private val _height = resources.getDimensionPixelOffset(R.dimen.hsv_size) + _padding * 2
    private val _sampleRadius = resources.getDimension(R.dimen.sample_radius)
    private val _sampleFrameRadius = _sampleRadius + resources.getDimension(R.dimen.sample_frame)
    private val _sampleShadowRadius =
        _sampleFrameRadius + resources.getDimension(R.dimen.sample_shadow)
    private val bitmapRect = Rect(0, 0, TONE_SIZE, TONE_SIZE)
    private val targetRect = Rect()
    private val hsv = FloatArray(3)
    private var hue: Float = 0f
    var saturation: Float = 0f
        private set
    var value: Float = 0f
        private set
    private var lastUpdate: Long = 0L
    private val setColorTask = Runnable {
        lastUpdate = System.currentTimeMillis()
        updateBitmap()
        invalidate()
    }
    private val colorSampleFrame = ContextCompat.getColor(context, R.color.sample_frame)
    private val colorSampleShadow = ContextCompat.getColor(context, R.color.sample_shadow)
    var onColorChanged: ((color: Int) -> Unit)? = null

    init {
        paint.isAntiAlias = true
        updateBitmap()
    }

    fun setColor(@ColorInt color: Int) {
        this.color = color
        ColorUtils.colorToHsv(color, hsv)
        updateHue(hsv[0])
        updateSv(hsv[1], hsv[2])
    }

    private fun updateHue(h: Float) {
        if (hue == h) {
            return
        }
        hue = h
        val callInterval = System.currentTimeMillis() - lastUpdate
        val delay = (INTERVAL - callInterval).clamp(0L, INTERVAL)
        removeCallbacks(setColorTask)
        postDelayed(setColorTask, delay)
    }

    private fun updateSv(s: Float, v: Float, fromUser: Boolean = false) {
        if (saturation == s && value == v) {
            return
        }
        saturation = s
        value = v
        invalidate()
        if (fromUser) {
            onColorChanged?.invoke(color)
        }
    }

    private fun updateBitmap() {
        for (y in 0 until TONE_SIZE) {
            for (x in 0 until TONE_SIZE) {
                pixels[x + y * TONE_SIZE] = ColorUtils.hsvToColor(
                    hue, x / TONE_MAX.toFloat(), (TONE_MAX - y) / TONE_MAX.toFloat()
                )
            }
        }
        bitmap.setPixels(pixels, 0, TONE_SIZE, 0, 0, TONE_SIZE, TONE_SIZE)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        hsv[1] = ((event.x - targetRect.left) / targetRect.width()).clamp(0f, 1f)
        hsv[2] = ((targetRect.bottom - event.y) / targetRect.height()).clamp(0f, 1f)
        color = ColorUtils.hsvToColor(hsv)
        updateSv(hsv[1], hsv[2], true)
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
        val x = saturation * targetRect.width() + targetRect.left
        val y = (1f - value) * targetRect.height() + targetRect.top
        paint.color = colorSampleShadow
        canvas.drawCircle(x, y, _sampleShadowRadius, paint)
        paint.color = colorSampleFrame
        canvas.drawCircle(x, y, _sampleFrameRadius, paint)
        paint.color = color
        canvas.drawCircle(x, y, _sampleRadius, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val paddingHorizontal = paddingLeft + paddingRight
        val paddingVertical = paddingTop + paddingBottom
        val resizeWidth = MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY
        val resizeHeight = MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY

        if (!resizeWidth && !resizeHeight) {
            setMeasuredDimension(
                resolveSizeAndState(
                    Math.max(_width + paddingHorizontal, suggestedMinimumWidth),
                    widthMeasureSpec,
                    MeasureSpec.UNSPECIFIED
                ),
                resolveSizeAndState(
                    Math.max(_height + paddingVertical, suggestedMinimumHeight),
                    heightMeasureSpec,
                    MeasureSpec.UNSPECIFIED
                )
            )
            return
        }

        var widthSize = resolveAdjustedSize(_width + paddingHorizontal, widthMeasureSpec)
        var heightSize = resolveAdjustedSize(_height + paddingVertical, heightMeasureSpec)
        val actualAspect =
            (widthSize - paddingHorizontal).toFloat() / (heightSize - paddingVertical)
        if (Math.abs(actualAspect - 1f) < 0.0000001) {
            setMeasuredDimension(widthSize, heightSize)
            return
        }
        if (resizeWidth) {
            val newWidth = heightSize - paddingVertical + paddingHorizontal
            if (!resizeHeight) {
                widthSize = resolveAdjustedSize(newWidth, widthMeasureSpec)
            }
            if (newWidth <= widthSize) {
                widthSize = newWidth
                setMeasuredDimension(widthSize, heightSize)
                return
            }
        }
        if (resizeHeight) {
            val newHeight = widthSize - paddingHorizontal + paddingVertical
            if (!resizeWidth) {
                heightSize = resolveAdjustedSize(newHeight, heightMeasureSpec)
            }
            if (newHeight <= heightSize) {
                heightSize = newHeight
            }
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    private fun resolveAdjustedSize(desiredSize: Int, measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (specMode) {
            MeasureSpec.UNSPECIFIED -> desiredSize
            MeasureSpec.AT_MOST -> Math.min(desiredSize, specSize)
            MeasureSpec.EXACTLY -> specSize
            else -> desiredSize
        }
    }

    companion object {
        private const val TONE_MAX = 255
        private const val TONE_SIZE = 256
        private const val INTERVAL = 100L
    }
}
