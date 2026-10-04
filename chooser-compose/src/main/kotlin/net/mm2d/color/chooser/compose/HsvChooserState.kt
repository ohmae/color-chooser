/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import net.mm2d.color.chooser.compose.util.toHsv

// グレーの色相や黒の彩度は RGB だけでは保持できないため、タブ切り替えより上位で状態を保持する。
@Stable
internal class HsvChooserState(
    color: Color,
) {
    private val hsvBuffer = color.toHsv()
    private val acceptedHsv = hsvBuffer.copyOf()
    private var acceptedColor = color
    var hue by mutableFloatStateOf(hsvBuffer[0])
        private set
    var saturation by mutableFloatStateOf(hsvBuffer[1])
        private set
    var value by mutableFloatStateOf(hsvBuffer[2])
        private set
    var lastColor by mutableStateOf(color)
        private set

    fun syncColor(
        color: Color,
    ) {
        if (color == lastColor) {
            acceptCoordinates(color)
            return
        }
        // 不採用の編集は直前の採用済み座標に戻し、黒の色相・彩度も維持する。
        val hsv = if (color == acceptedColor) acceptedHsv else color.toHsv(hsvBuffer)
        hue = hsv[0]
        saturation = hsv[1]
        value = hsv[2]
        acceptCoordinates(color)
        lastColor = color
    }

    private fun acceptCoordinates(
        color: Color,
    ) {
        acceptedHsv[0] = hue
        acceptedHsv[1] = saturation
        acceptedHsv[2] = value
        acceptedColor = color
    }

    fun update(
        hue: Float,
        saturation: Float,
        value: Float,
    ): Color {
        this.hue = hue
        this.saturation = saturation
        this.value = value
        return Color.hsv(hue, saturation, value).also {
            lastColor = it
            // RGB が変わらない編集座標は、外部色の更新なしでも保持できる。
            if (it == acceptedColor) acceptCoordinates(it)
        }
    }

    companion object {
        val Saver = listSaver<HsvChooserState, Any>(
            save = { listOf(it.acceptedHsv[0], it.acceptedHsv[1], it.acceptedHsv[2], it.acceptedColor.toArgb()) },
            restore = {
                HsvChooserState(Color(it[3] as Int)).apply {
                    hue = it[0] as Float
                    saturation = it[1] as Float
                    value = it[2] as Float
                    acceptCoordinates(lastColor)
                }
            },
        )
    }
}

@Composable
internal fun rememberHsvChooserState(
    color: Color,
): HsvChooserState {
    val state = rememberSaveable(saver = HsvChooserState.Saver) { HsvChooserState(color) }
    SideEffect(color, state.lastColor) { state.syncColor(color) }
    return state
}
