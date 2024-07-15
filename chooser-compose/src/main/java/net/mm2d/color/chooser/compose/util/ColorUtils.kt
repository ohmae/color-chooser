/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose.util

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb

/**
 * Minimum contrast for large text based on W3C guideline
 *
 * https://www.w3.org/TR/WCAG20/#visual-audio-contrast-contrast
 */
private const val MINIMUM_CONTRAST_FOR_LARGE_TEXT = 3f

fun Color.shouldUseWhiteForeground(): Boolean =
    calculateContrastRatio(this, Color.White) > MINIMUM_CONTRAST_FOR_LARGE_TEXT

fun calculateContrastRatio(foreground: Color, background: Color): Float {
    val foregroundLuminance = foreground.luminance() + 0.05f
    val backgroundLuminance = background.luminance() + 0.05f

    return maxOf(foregroundLuminance, backgroundLuminance) /
        minOf(foregroundLuminance, backgroundLuminance)
}

fun Color.toHsv(outHsv: FloatArray? = null): FloatArray {
    val r = red
    val g = green
    val b = blue
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val hsv = outHsv ?: FloatArray(3)
    hsv[0] = hue(r, g, b, max, min)
    hsv[1] = saturation(max, min)
    hsv[2] = max
    return hsv
}

private fun hue(r: Float, g: Float, b: Float, max: Float, min: Float): Float {
    val range = max - min
    if (range == 0f) return 0f
    val hue = when (max) {
        r -> ((g - b) / range).let { if (it < 0f) it + 6f else it }
        g -> (b - r) / range + 2f
        else -> (r - g) / range + 4f
    }
    return (hue * 60f).coerceIn(0f, 360f)
}

private fun saturation(max: Float, min: Float): Float =
    if (max != 0f) (max - min) / max else 0f

object ColorSaver : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)
    override fun SaverScope.save(value: Color): Int = value.toArgb()
}
