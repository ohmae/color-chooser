/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.colorchooser.util

/**
 * HSVやRGBの色空間表現を扱う上でのメソッド
 *
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
object ColorUtils {
    fun hsvToColor(hsv: FloatArray): Int =
        hsvToColor(hsv[0], hsv[1], hsv[2])

    fun hsvToColor(h: Float, s: Float, v: Float): Int {
        if (s <= 0f) return toColor(v, v, v)
        val hue = h * 6f // [0.0f, 6.0f]
        val i = hue.toInt() // hueの整数部
        val d = hue - i // hueの小数部
        var r = v
        var g = v
        var b = v
        when (i) {
            0 -> { // h:[0.0f, 1.0f)
                g *= 1f - s * (1f - d)
                b *= 1f - s
            }
            1 -> { // h:[1.0f, 2.0f)
                r *= 1f - s * d
                b *= 1f - s
            }
            2 -> { // h:[2.0f, 3.0f)
                r *= 1f - s
                b *= 1f - s * (1f - d)
            }
            3 -> { // h:[3.0f, 4.0f)
                r *= 1f - s
                g *= 1f - s * d
            }
            4 -> { // h:[4.0f, 5.0f)
                r *= 1f - s * (1f - d)
                g *= 1f - s
            }
            5 -> { // h:[5.0f, 6.0f)
                g *= 1f - s
                b *= 1f - s * d
            }
            else -> {
                g *= 1f - s * (1f - d)
                b *= 1f - s
            }
        }
        return toColor(r, g, b)
    }

    fun svToMask(s: Float, v: Float): Int {
        val a = 1f - (s * v)
        val g = if (a == 0f) 0f else (v * (1f - s) / a).clamp(0f, 1f)
        return toColor(a, g, g, g)
    }

    fun colorToHsv(color: Int, outHsv: FloatArray? = null): FloatArray {
        val r = color.red / 255f
        val g = color.green / 255f
        val b = color.blue / 255f
        val max = max(r, g, b)
        val min = min(r, g, b)
        val hsv = outHsv ?: FloatArray(3)
        hsv[0] = hue(r, g, b, max, min)
        hsv[1] = saturation(max, min)
        hsv[2] = max
        return hsv
    }

    fun hue(color: Int): Float {
        val r = color.red / 255f
        val g = color.green / 255f
        val b = color.blue / 255f
        val max = max(r, g, b)
        val min = min(r, g, b)
        return hue(r, g, b, max, min)
    }

    private fun max(v1: Float, v2: Float, v3: Float): Float {
        val max = if (v1 > v2) v1 else v2
        return if (max > v3) max else v3
    }

    private fun min(v1: Float, v2: Float, v3: Float): Float {
        val min = if (v1 < v2) v1 else v2
        return if (min < v3) min else v3
    }

    private fun hue(r: Float, g: Float, b: Float, max: Float, min: Float): Float {
        val range = max - min
        if (range == 0f) return 0f
        val hue = when (max) {
            r -> ((g - b) / range).let { if (it < 0f) it + 6f else it }
            g -> (b - r) / range + 2f
            else -> (r - g) / range + 4f
        }
        return (hue / 6f).clamp(0f, 1f)
    }

    private fun saturation(max: Float, min: Float): Float =
        if (max != 0.0f) (max - min) / max else 0f

    fun toColor(r: Float, g: Float, b: Float): Int =
        toColor(r.to8bit(), g.to8bit(), b.to8bit())

    fun toColor(r: Int, g: Int, b: Int): Int =
        (0xff shl 24) or (0xff and r shl 16) or (0xff and g shl 8) or (0xff and b)

    fun toColor(a: Float, r: Float, g: Float, b: Float): Int =
        toColor(a.to8bit(), r.to8bit(), g.to8bit(), b.to8bit())

    fun toColor(a: Int, r: Int, g: Int, b: Int): Int =
        (0xff and a shl 24) or (0xff and r shl 16) or (0xff and g shl 8) or (0xff and b)

    fun toRgb(rgb: IntArray): FloatArray =
        toRgb(rgb[0], rgb[1], rgb[2])

    fun toRgb(color: Int): FloatArray =
        toRgb(color.red, color.green, color.blue)

    fun toRgb(r: Int, g: Int, b: Int): FloatArray =
        floatArrayOf(r.toRatio(), g.toRatio(), b.toRatio())

    /**
     * Based on ITU-R BT.709
     */
    fun luminance(r: Int, g: Int, b: Int): Int =
        (r * 0.2126f + g * 0.7152f + b * 0.0722f + 0.5f).toInt().clamp(0, 255)

    /**
     * https://www.w3.org/TR/WCAG20/#relativeluminancedef
     */
    fun luminance(r: Float, g: Float, b: Float): Float =
        r * 0.2126f + g * 0.7152f + b * 0.0722f

    /**
     * https://www.w3.org/TR/WCAG20/#contrast-ratiodef
     * Contrast ratios can range from 1 to 21 (commonly written 1:1 to 21:1).
     */
    fun contrast(color1: Int, color2: Int): Float {
        val l1 = color1.relativeLuminance()
        val l2 = color2.relativeLuminance()
        return if (l1 > l2) (l1 + 0.05f) / (l2 + 0.05f) else (l2 + 0.05f) / (l1 + 0.05f)
    }

    /**
     * https://www.w3.org/TR/WCAG20/#visual-audio-contrast-contrast
     */
    private const val MINUMUM_CONTRAST = 4.5f
    /**
     * https://www.w3.org/TR/WCAG20/#visual-audio-contrast-contrast
     */
    private const val MINIMUM_CONTRAST_FOR_LARGE_TEXT = 3f

    fun shoulUseWhiteForeground(color: Int): Boolean =
        color.contrastWithWhite() > MINIMUM_CONTRAST_FOR_LARGE_TEXT
}

val Int.red: Int
    get() = ushr(16) and 0xff
val Int.green: Int
    get() = ushr(8) and 0xff
val Int.blue: Int
    get() = this and 0xff

fun Int.setAlpha(alpha: Float): Int = setAlpha((0xff * alpha.clamp(0f, 1f)).toInt())

fun Int.setAlpha(alpha: Int): Int = this and 0xffffff or (alpha shl 24)

fun Int.angleToRatio(): Float = (this / 360f).clamp(0f, 1f)

fun Float.ratioToAngle(): Int = (this * 360f + 0.5f).toInt().clamp(0, 360)

fun Int.toRatio(): Float = this / 255f

fun Float.to8bit(): Int = (this * 255f + 0.5f).toInt().clamp(0, 255)

fun Int.luminance(): Int = ColorUtils.luminance(red, green, blue)

/**
 * https://www.w3.org/TR/WCAG20/#relativeluminancedef
 */
fun Float.normalizeForSrgb(): Float =
    if (this < 0.03928f) this / 12.92f else Math.pow((this + 0.055f) / 1.055, 2.4).toFloat()

fun Int.normalizeForSrbg(): Float = toRatio().normalizeForSrgb()

fun Int.relativeLuminance(): Float {
    return ColorUtils.luminance(
        red.normalizeForSrbg(),
        green.normalizeForSrbg(),
        blue.normalizeForSrbg()
    )
}

fun Int.contrastWithWhite(): Float {
    return 1.05f / (this.relativeLuminance() + 0.05f)
}
