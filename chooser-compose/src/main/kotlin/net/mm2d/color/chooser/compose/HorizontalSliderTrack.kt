/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.util.CONTROL_GRIP_RADIUS
import net.mm2d.color.chooser.compose.util.ChooserShapes
import net.mm2d.color.chooser.compose.util.ControlInteraction
import net.mm2d.color.chooser.compose.util.SliderGrip
import net.mm2d.color.chooser.compose.util.TRACK_HEIGHT
import net.mm2d.color.chooser.compose.util.controlInteraction
import net.mm2d.color.chooser.compose.util.detectHorizontalTapAndDragGestures
import net.mm2d.color.chooser.compose.util.frameDecoration
import net.mm2d.color.chooser.compose.util.ratio
import kotlin.math.roundToInt

// 水平スライダーの描画・測定・操作検出。値の単位と変換は呼び出し側で扱う。
@Composable
internal fun HorizontalSliderTrack(
    currentRatio: Float,
    onRatioChange: (Float) -> Unit,
    colorBrush: Brush,
    gripColor: Color,
    modifier: Modifier = Modifier,
    accessibilityModifier: Modifier,
    interaction: ControlInteraction = remember { ControlInteraction() },
    trackBackground: Modifier = Modifier,
    content: (@Composable BoxScope.() -> Unit)? = null,
) {
    val currentOnRatioChange by rememberUpdatedState(onRatioChange)
    val density = LocalDensity.current
    val gripRadiusPx = remember(density) {
        with(density) { CONTROL_GRIP_RADIUS.roundToPx() }
    }
    var trackWidthPx by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .height(TRACK_HEIGHT)
            .systemGestureExclusion()
            .onSizeChanged { trackWidthPx = it.width },
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 6.dp)
                .fillMaxSize()
                .frameDecoration(ChooserShapes.track)
                .then(trackBackground)
                .background(colorBrush),
        )
        content?.invoke(this)
        SliderGrip(
            active = interaction.active,
            color = gripColor,
            modifier = Modifier
                .align(AbsoluteAlignment.TopLeft)
                .absoluteOffset {
                    val rangeXPx = (trackWidthPx - gripRadiusPx * 2).coerceAtLeast(0)
                    val x = (rangeXPx * currentRatio).roundToInt()
                    IntOffset(x = x, y = 0)
                },
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .controlInteraction(interaction)
                .then(accessibilityModifier)
                .pointerInput(trackWidthPx, density) {
                    if (trackWidthPx <= 0) return@pointerInput
                    val rangeXPx = (trackWidthPx - gripRadiusPx * 2).coerceAtLeast(0)
                    if (rangeXPx == 0) return@pointerInput
                    detectHorizontalTapAndDragGestures { position ->
                        val targetX = position.x - gripRadiusPx
                        val ratio = ratio(targetX, rangeXPx.toFloat())
                        currentOnRatioChange(ratio)
                    }
                },
        )
    }
}

// キーボードとアクセシビリティに共通する範囲操作。整数への丸めは呼び出し側で指定する。
internal fun Modifier.sliderAccessibility(
    label: String,
    value: Float,
    maxValue: Float,
    steps: Int,
    onValueChange: (Float) -> Unit,
    normalizeValue: (Float) -> Float = { it },
): Modifier {
    val updateValue = { requested: Float ->
        if (!requested.isFinite()) {
            false
        } else {
            val newValue = normalizeValue(requested).coerceIn(0f, maxValue)
            if (newValue == value) {
                false
            } else {
                onValueChange(newValue)
                true
            }
        }
    }
    return this
        .semantics {
            contentDescription = label
            progressBarRangeInfo = ProgressBarRangeInfo(value, 0f..maxValue, steps)
            setProgress(action = updateValue)
        }
        .onKeyEvent {
            if (it.type != KeyEventType.KeyDown) return@onKeyEvent false
            val target = when (it.key) {
                Key.DirectionRight, Key.DirectionUp -> value + 1f
                Key.DirectionLeft, Key.DirectionDown -> value - 1f
                Key.MoveHome -> 0f
                Key.MoveEnd -> maxValue
                else -> return@onKeyEvent false
            }
            updateValue(target)
            true
        }
        .focusable()
}
