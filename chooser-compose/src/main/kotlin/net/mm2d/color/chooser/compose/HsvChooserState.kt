/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import net.mm2d.color.chooser.compose.util.toHsv

private data class HsvCoordinates(
    val hue: Float,
    val saturation: Float,
    val value: Float,
) {
    companion object {
        fun fromColor(
            color: Color,
            buffer: FloatArray? = null,
        ): HsvCoordinates {
            val (hue, saturation, value) = color.toHsv(buffer)
            return HsvCoordinates(hue, saturation, value)
        }
    }
}

private data class HsvSelection(
    val coordinates: HsvCoordinates,
    val color: Color,
)

// グレーの色相や黒の彩度は RGB だけでは保持できないため、タブ切り替えより上位で状態を保持する。
@Stable
internal class HsvChooserState private constructor(
    initialSelection: HsvSelection,
) {
    constructor(color: Color) : this(HsvSelection(HsvCoordinates.fromColor(color), color))

    private val hsvBuffer = FloatArray(3)
    private var editingSelection by mutableStateOf(initialSelection)
    private var acceptedSelection = initialSelection

    val hue: Float get() = editingSelection.coordinates.hue
    val saturation: Float get() = editingSelection.coordinates.saturation
    val value: Float get() = editingSelection.coordinates.value
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val editingColor: Color get() = editingSelection.color

    fun syncColor(
        color: Color,
    ) {
        val selection = when (color) {
            // 外部が編集色を採用した場合は、RGB から復元できない座標もそのまま採用する。
            editingSelection.color -> editingSelection

            // 不採用の編集は採用済みの座標に戻し、黒の色相・彩度も維持する。
            acceptedSelection.color -> acceptedSelection

            // 編集とは異なる外部色への更新では、HSV 座標も更新する。
            else -> HsvSelection(HsvCoordinates.fromColor(color, hsvBuffer), color)
        }
        editingSelection = selection
        acceptedSelection = selection
    }

    fun update(
        hue: Float,
        saturation: Float,
        value: Float,
    ): Color {
        val selection = HsvSelection(HsvCoordinates(hue, saturation, value), Color.hsv(hue, saturation, value))
        editingSelection = selection
        // RGB が変わらない編集座標は、外部色の更新なしでも保持できる。
        if (selection.color == acceptedSelection.color) acceptedSelection = selection
        return selection.color
    }

    companion object {
        // 既存の保存形式（色相・彩度・明度・ARGB）を維持し、採用済みの状態だけを保存する。
        val Saver = listSaver<HsvChooserState, Number>(
            save = {
                val accepted = it.acceptedSelection
                listOf(
                    accepted.coordinates.hue,
                    accepted.coordinates.saturation,
                    accepted.coordinates.value,
                    accepted.color.toArgb(),
                )
            },
            restore = {
                val coordinates = HsvCoordinates(it[0].toFloat(), it[1].toFloat(), it[2].toFloat())
                val color = Color(it[3].toInt())
                HsvChooserState(HsvSelection(coordinates, color))
            },
        )
    }
}

@Composable
internal fun rememberHsvChooserState(
    color: Color,
): HsvChooserState {
    val state = rememberSaveable(saver = HsvChooserState.Saver) { HsvChooserState(color) }
    SideEffect(color, state.editingColor) { state.syncColor(color) }
    return state
}
