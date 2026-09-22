/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.util.CONTROL_GRIP_RADIUS
import net.mm2d.color.chooser.compose.util.ControlGrip
import net.mm2d.color.chooser.compose.util.detectTapAndDragGestures
import net.mm2d.color.chooser.compose.util.frameDecoration
import net.mm2d.color.chooser.compose.util.ratio
import kotlin.math.roundToInt

private const val HUE_MAX = 360f
private val TRACK_HEIGHT = 32.dp

@Composable
internal fun HsvChooser(
    currentColor: Color,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier,
    state: HsvChooserState = rememberHsvChooserState(currentColor),
) {
    val currentOnColorChanged by rememberUpdatedState(onColorChanged)
    val hue = state.hue
    val saturation = state.saturation
    val value = state.value
    val updateHue = { newHue: Float ->
        currentOnColorChanged(state.update(newHue, state.saturation, state.value))
    }
    val updateSv = { newSaturation: Float, newValue: Float ->
        currentOnColorChanged(state.update(state.hue, newSaturation, newValue))
    }
    val svLabel = stringResource(R.string.mm2d_cc_saturation_value)
    val svState = stringResource(
        R.string.mm2d_cc_saturation_value_state,
        (saturation * 100f).roundToInt(),
        (value * 100f).roundToInt(),
    )
    val adjustSv = { saturationDelta: Float, valueDelta: Float ->
        val newSaturation = (saturation + saturationDelta).coerceIn(0f, 1f)
        val newValue = (value + valueDelta).coerceIn(0f, 1f)
        if (newSaturation == saturation && newValue == value) {
            false
        } else {
            updateSv(newSaturation, newValue)
            true
        }
    }
    val svActions = listOf(
        CustomAccessibilityAction(stringResource(R.string.mm2d_cc_increase_saturation)) { adjustSv(0.01f, 0f) },
        CustomAccessibilityAction(stringResource(R.string.mm2d_cc_decrease_saturation)) { adjustSv(-0.01f, 0f) },
        CustomAccessibilityAction(stringResource(R.string.mm2d_cc_increase_value)) { adjustSv(0f, 0.01f) },
        CustomAccessibilityAction(stringResource(R.string.mm2d_cc_decrease_value)) { adjustSv(0f, -0.01f) },
    )
    val density = LocalDensity.current
    val gripRadiusPx = remember(density) {
        with(density) { CONTROL_GRIP_RADIUS.roundToPx() }
    }
    val topMarginPx = remember(density) {
        with(density) { (TRACK_HEIGHT / 2 - CONTROL_GRIP_RADIUS).roundToPx() }
    }
    var trackWidthPx by remember { mutableIntStateOf(0) }
    var svSizePx by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TRACK_HEIGHT)
                .onSizeChanged { trackWidthPx = it.width },
        ) {
            val currentRatio = (hue / HUE_MAX).coerceIn(0f, 1f)

            val colorBrush = remember {
                val grid = 36
                Brush.horizontalGradient(
                    (0..grid).map {
                        Color.hsv(it.toFloat() / grid * HUE_MAX, 1f, 1f)
                    },
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 5.dp)
                    .frameDecoration()
                    .fillMaxSize()
                    .background(colorBrush),
            )
            ControlGrip(
                color = Color.hsv(hue = hue, saturation = 1f, value = 1f),
                modifier = Modifier
                    .align(AbsoluteAlignment.TopLeft)
                    .absoluteOffset {
                        val rangeXPx = (trackWidthPx - gripRadiusPx * 2).coerceAtLeast(0)
                        val x = (rangeXPx * currentRatio).roundToInt()
                        IntOffset(x = x, y = topMarginPx)
                    },
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .hueAccessibility(stringResource(R.string.mm2d_cc_hue), hue, updateHue)
                    .pointerInput(trackWidthPx, density) {
                        if (trackWidthPx <= 0) return@pointerInput
                        val rangeXPx = (trackWidthPx - gripRadiusPx * 2).coerceAtLeast(0)
                        if (rangeXPx == 0) return@pointerInput
                        detectTapAndDragGestures { position ->
                            val targetX = position.x - gripRadiusPx
                            val ratio = ratio(targetX, rangeXPx.toFloat())
                            updateHue(ratio * 360f)
                        }
                    },
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            val horizontalBrush = remember(hue) {
                Brush.horizontalGradient(
                    listOf(Color.White, Color.hsv(hue, 1f, 1f)),
                )
            }
            val verticalBrush = remember {
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Black),
                )
            }

            Box(
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val squareSize = minOf(constraints.maxWidth, constraints.maxHeight).coerceAtLeast(0)
                        val placeable = measurable.measure(
                            Constraints.fixed(squareSize, squareSize),
                        )
                        layout(squareSize, squareSize) {
                            placeable.place(0, 0)
                        }
                    }
                    .onSizeChanged { svSizePx = it.width },
            ) {
                Canvas(
                    modifier = Modifier
                        .padding(5.dp)
                        .frameDecoration()
                        .fillMaxSize(),
                ) {
                    drawRect(brush = horizontalBrush)
                    drawRect(brush = verticalBrush)
                }
                ControlGrip(
                    color = Color.hsv(hue, saturation, value),
                    modifier = Modifier
                        .align(AbsoluteAlignment.TopLeft)
                        .absoluteOffset {
                            val rangeSizePx = (svSizePx - gripRadiusPx * 2).coerceAtLeast(0)
                            val x = (rangeSizePx * saturation.coerceIn(0f, 1f)).roundToInt()
                            val y = (rangeSizePx * (1f - value).coerceIn(0f, 1f)).roundToInt()
                            IntOffset(x = x, y = y)
                        },
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics {
                            contentDescription = svLabel
                            stateDescription = svState
                            customActions = svActions
                        }
                        .onKeyEvent {
                            if (it.type != KeyEventType.KeyDown) return@onKeyEvent false
                            when (it.key) {
                                Key.DirectionRight -> adjustSv(0.01f, 0f)
                                Key.DirectionLeft -> adjustSv(-0.01f, 0f)
                                Key.DirectionUp -> adjustSv(0f, 0.01f)
                                Key.DirectionDown -> adjustSv(0f, -0.01f)
                                else -> return@onKeyEvent false
                            }
                            true
                        }
                        .focusable()
                        .pointerInput(svSizePx, density) {
                            if (svSizePx <= 0) return@pointerInput
                            val rangeSizePx = (svSizePx - gripRadiusPx * 2).coerceAtLeast(0)
                            if (rangeSizePx == 0) return@pointerInput
                            detectTapAndDragGestures { position ->
                                val targetX = position.x - gripRadiusPx
                                val targetY = position.y - gripRadiusPx
                                val newSaturation = ratio(targetX, rangeSizePx.toFloat())
                                val newValue = 1f - ratio(targetY, rangeSizePx.toFloat())
                                updateSv(newSaturation, newValue)
                            }
                        },
                )
            }
        }
    }
}

internal fun Modifier.hueAccessibility(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
): Modifier {
    val update = { requested: Float ->
        if (!requested.isFinite()) {
            false
        } else {
            val newValue = requested.coerceIn(0f, HUE_MAX)
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
            progressBarRangeInfo = ProgressBarRangeInfo(value, 0f..HUE_MAX, 359)
            setProgress(action = update)
        }
        .onKeyEvent {
            if (it.type != KeyEventType.KeyDown) return@onKeyEvent false
            val target = when (it.key) {
                Key.DirectionRight, Key.DirectionUp -> value + 1f
                Key.DirectionLeft, Key.DirectionDown -> value - 1f
                Key.MoveHome -> 0f
                Key.MoveEnd -> HUE_MAX
                else -> return@onKeyEvent false
            }
            update(target)
            true
        }
        .focusable()
}
