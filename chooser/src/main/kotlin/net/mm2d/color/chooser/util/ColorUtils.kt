/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.util

import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.pow

/**
 * HSVやRGBの色空間表現を扱う上でのメソッド
 */
internal object ColorUtils {
    /**
     * [0.0f, 1.0f] の範囲の HSV を色に変換する
     *
     * @param h 色相
     * @param s 彩度
     * @param v 明度
     * @return 色
     */
    fun hsvToColor(
        h: Float,
        s: Float,
        v: Float,
    ): Int {
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

    /**
     * [0.0f, 1.0f] の範囲の SV をモノクロとアルファ値のマスクに変換する
     *
     * @param s 彩度
     * @param v 明度
     * @return マスクの画素値
     */
    fun svToMask(
        s: Float,
        v: Float,
    ): Int {
        val a = 1f - (s * v)
        val g = if (a == 0f) 0f else (v * (1f - s) / a).coerceIn(0f, 1f)
        return toColor(a, g, g, g)
    }

    /**
     * 色を [0.0f, 1.0f] の範囲の HSV 配列に変換する
     *
     * @param color 色
     * @param outHsv HSV の出力先。未指定または null の場合は新しい配列を確保する
     * @return HSV 配列
     */
    fun colorToHsv(
        color: Int,
        outHsv: FloatArray? = null,
    ): FloatArray {
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

    /**
     * 色相を計算する
     *
     * @param color 色
     * @return 色相
     */
    fun hue(
        color: Int,
    ): Float {
        val r = color.red / 255f
        val g = color.green / 255f
        val b = color.blue / 255f
        val max = max(r, g, b)
        val min = min(r, g, b)
        return hue(r, g, b, max, min)
    }

    private fun max(
        v1: Float,
        v2: Float,
        v3: Float,
    ): Float = maxOf(maxOf(v1, v2), v3)

    private fun min(
        v1: Float,
        v2: Float,
        v3: Float,
    ): Float = minOf(minOf(v1, v2), v3)

    private fun hue(
        r: Float,
        g: Float,
        b: Float,
        max: Float,
        min: Float,
    ): Float {
        val range = max - min
        if (range == 0f) return 0f
        val hue = when (max) {
            r -> ((g - b) / range).let { if (it < 0f) it + 6f else it }
            g -> (b - r) / range + 2f
            else -> (r - g) / range + 4f
        }
        return (hue / 6f).coerceIn(0f, 1f)
    }

    private fun saturation(
        max: Float,
        min: Float,
    ): Float = if (max != 0.0f) (max - min) / max else 0f

    /**
     * [0.0f, 1.0f] の範囲の ARGB 値を色に変換する
     *
     * @param r 赤の値
     * @param g 緑の値
     * @param b 青の値
     * @return 色
     */
    private fun toColor(
        r: Float,
        g: Float,
        b: Float,
    ): Int = toColor(r.to8bit(), g.to8bit(), b.to8bit())

    /**
     * [0, 255] の範囲の RGB 値を色に変換する
     *
     * @param r 赤の値
     * @param g 緑の値
     * @param b 青の値
     * @return 色
     */
    private fun toColor(
        r: Int,
        g: Int,
        b: Int,
    ): Int = (0xff shl 24) or (0xff and r shl 16) or (0xff and g shl 8) or (0xff and b)

    /**
     * [0.0f, 1.0f] の範囲の ARGB 値を色に変換する
     *
     * @param a アルファ値
     * @param r 赤の値
     * @param g 緑の値
     * @param b 青の値
     * @return 色
     */
    private fun toColor(
        a: Float,
        r: Float,
        g: Float,
        b: Float,
    ): Int =
        toColor(
            a.to8bit(),
            r.to8bit(),
            g.to8bit(),
            b.to8bit(),
        )

    /**
     * [0, 255] の範囲の ARGB 値を色に変換する
     *
     * @param a アルファ値
     * @param r 赤の値
     * @param g 緑の値
     * @param b 青の値
     * @return 色
     */
    private fun toColor(
        a: Int,
        r: Int,
        g: Int,
        b: Int,
    ): Int = (0xff and a shl 24) or (0xff and r shl 16) or (0xff and g shl 8) or (0xff and b)

    /**
     * ITU-R BT.709 と sRGB に基づいて輝度を計算する
     *
     * https://www.w3.org/TR/WCAG20/#relativeluminancedef
     *
     * @param r 赤の比率
     * @param g 緑の比率
     * @param b 青の比率
     * @return 輝度
     */
    fun luminance(
        r: Float,
        g: Float,
        b: Float,
    ): Float = r * 0.2126f + g * 0.7152f + b * 0.0722f

    /**
     * W3C のガイドラインに基づく、大きな文字に必要な最小コントラスト
     *
     * https://www.w3.org/TR/WCAG20/#visual-audio-contrast-contrast
     */
    private const val MINIMUM_CONTRAST_FOR_LARGE_TEXT = 3f

    /**
     * 白い前景色で十分なコントラストを確保できるか判定する。
     *
     * @param color
     * @return true の場合は白い前景色を使用し、false の場合は白い前景色を避ける
     */
    fun shouldUseWhiteForeground(
        color: Int,
    ): Boolean = color.contrastWithWhite() > MINIMUM_CONTRAST_FOR_LARGE_TEXT
}

/**
 * 色のアルファ値を上書きする
 *
 * @receiver 色
 * @param alpha アルファ値
 * @return アルファ値を適用した色
 */
internal fun Int.setAlpha(
    alpha: Int,
): Int = this and 0xffffff or (alpha shl 24)

/**
 * アルファ値を完全に不透明な値に上書きする
 */
internal fun Int.toOpacity(): Int = setAlpha(0xff)

/**
 * [0, 255] を [0.0f, 1.0f] に変換する
 *
 * @receiver [0, 255]
 * @return [0.0f, 1.0f]
 */
internal fun Int.toRatio(): Float = this / 255f

/**
 * [0.0f, 1.0f] を [0, 255] に変換する
 *
 * @receiver [0.0f, 1.0f]
 * @return [0, 255]
 */
internal fun Float.to8bit(): Int = (this * 255f + 0.5f).toInt().coerceIn(0, 255)

/**
 * 色の sRGB 輝度を計算するために、原色の輝度値を正規化する
 *
 * https://www.w3.org/TR/WCAG20/#relativeluminancedef
 *
 * @receiver 原色の輝度
 * @return 正規化した輝度
 */
internal fun Float.normalizeForSrgb(): Float =
    if (this < 0.03928f) this / 12.92f else ((this + 0.055) / 1.055).pow(2.4).toFloat()

/**
 * 色の sRGB 輝度を計算するために、原色の輝度値を正規化する
 *
 * https://www.w3.org/TR/WCAG20/#relativeluminancedef
 *
 * @receiver 原色の輝度
 * @return 正規化した輝度
 */
internal fun Int.normalizeForSrgb(): Float = toRatio().normalizeForSrgb()

/**
 * 色の sRGB 輝度を計算する
 *
 * @receiver 色
 * @return sRGB 輝度
 */
internal fun Int.relativeLuminance(): Float =
    ColorUtils.luminance(
        red.normalizeForSrgb(),
        green.normalizeForSrgb(),
        blue.normalizeForSrgb(),
    )

/**
 * 色と純白（#ffffff）とのコントラストを計算する
 *
 * @receiver 色
 * @return [1, 21] の範囲のコントラスト
 */
internal fun Int.contrastWithWhite(): Float = 1.05f / (relativeLuminance() + 0.05f)
