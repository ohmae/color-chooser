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
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import net.mm2d.color.chooser.compose.util.toHsv

// Owned above the tab switch: RGB alone cannot preserve hue at gray or saturation at black.
@Stable
internal class HsvChooserState(
    color: Color,
) {
    private val hsv = color.toHsv()
    var hue by mutableFloatStateOf(hsv[0])
        private set
    var saturation by mutableFloatStateOf(hsv[1])
        private set
    var value by mutableFloatStateOf(hsv[2])
        private set
    private var lastColor = color

    fun syncColor(
        color: Color,
    ) {
        if (color == lastColor) return
        color.toHsv(hsv)
        hue = hsv[0]
        saturation = hsv[1]
        value = hsv[2]
        lastColor = color
    }

    fun update(
        hue: Float,
        saturation: Float,
        value: Float,
    ): Color {
        this.hue = hue
        this.saturation = saturation
        this.value = value
        return Color.hsv(hue, saturation, value).also { lastColor = it }
    }

    companion object {
        val Saver = listSaver<HsvChooserState, Any>(
            save = { listOf(it.hue, it.saturation, it.value, it.lastColor.toArgb()) },
            restore = {
                HsvChooserState(Color(it[3] as Int)).apply {
                    hue = it[0] as Float
                    saturation = it[1] as Float
                    value = it[2] as Float
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
    SideEffect(color) { state.syncColor(color) }
    return state
}
