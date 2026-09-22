/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.MeshGradientPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import net.mm2d.color.chooser.compose.util.ColorControlGrip
import net.mm2d.color.chooser.compose.util.frameDecoration
import net.mm2d.color.chooser.compose.util.ratio
import net.mm2d.color.chooser.compose.util.toHsv
import kotlin.math.roundToInt

private const val HUE_MAX = 360f

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun HsvChooser(
    currentColor: Color,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnColorChanged by rememberUpdatedState(onColorChanged)
    val hsvBuffer = remember { FloatArray(3).also { currentColor.toHsv(it) } }
    var hue by remember { mutableFloatStateOf(hsvBuffer[0]) }
    var saturation by remember { mutableFloatStateOf(hsvBuffer[1]) }
    var value by remember { mutableFloatStateOf(hsvBuffer[2]) }

    var lastEmittedColor by remember { mutableStateOf<Color?>(null) }

    val updateHue = remember {
        { newHue: Float ->
            hue = newHue
            val newColor = Color.hsv(newHue, saturation, value)
            lastEmittedColor = newColor
            currentOnColorChanged(newColor)
        }
    }
    val updateSv = remember {
        { newSat: Float, newVal: Float ->
            saturation = newSat
            value = newVal
            val newColor = Color.hsv(hue, newSat, newVal)
            lastEmittedColor = newColor
            currentOnColorChanged(newColor)
        }
    }
    SideEffect(currentColor) {
        if (currentColor != lastEmittedColor) {
            currentColor.toHsv(hsvBuffer)
            hue = hsvBuffer[0]
            saturation = hsvBuffer[1]
            value = hsvBuffer[2]
            lastEmittedColor = currentColor
        }
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
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
        ) {
            val trackWidth = (maxWidth - 16.dp).coerceAtLeast(0.dp)
            val currentRatio = (hue / HUE_MAX).coerceIn(0f, 1f)
            val gripStartX = trackWidth * currentRatio

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
            ColorControlGrip(
                color = Color.hsv(hue = hue, saturation = 1f, value = 1f),
                modifier = Modifier
                    .align(AbsoluteAlignment.TopLeft)
                    .absoluteOffset(x = gripStartX, y = 8.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .hueAccessibility(stringResource(R.string.mm2d_cc_hue), hue, updateHue)
                    .pointerInput(trackWidth) {
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            val updatePosition = { position: Offset ->
                                val targetX = (position.x.toDp() - 8.dp).coerceIn(0.dp, trackWidth)
                                val newHue = ratio(targetX, trackWidth) * 360f
                                updateHue(newHue)
                            }
                            updatePosition(down.position)
                            down.consume()
                            do {
                                val event = awaitPointerEvent()
                                val change = event.changes.first()
                                updatePosition(change.position)
                                change.consume()
                            } while (event.changes.fastAny { it.pressed })
                        }
                    },
            )
        }
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            val maxColor = Color.hsv(hue, 1f, 1f)
            val gradientPainter = remember(maxColor) {
                val grid = 16
                val step = 1f / grid
                MeshGradientPainter(grid, grid) {
                    repeat(grid + 1) { y ->
                        repeat(grid + 1) { x ->
                            setVertex(
                                x,
                                y,
                                Offset(x * step, y * step),
                                Color.hsv(hue = hue, saturation = x * step, value = 1 - y * step),
                            )
                        }
                    }
                }
            }
            val size = minOf(maxWidth, maxHeight).coerceAtLeast(0.dp)
            val rangeSize = (size - 16.dp).coerceAtLeast(0.dp)
            val gripStartX = rangeSize * saturation.coerceIn(0f, 1f)
            val gripStartY = rangeSize * (1f - value).coerceIn(0f, 1f)

            Box(
                modifier = Modifier.size(size),
            ) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .frameDecoration()
                        .fillMaxSize()
                        .paint(gradientPainter),
                )
                ColorControlGrip(
                    color = Color.hsv(hue, saturation, value),
                    modifier = Modifier
                        .align(AbsoluteAlignment.TopLeft)
                        .absoluteOffset(x = gripStartX, y = gripStartY),
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
                        .pointerInput(rangeSize) {
                            awaitEachGesture {
                                val down = awaitFirstDown()
                                val updatePosition = { position: Offset ->
                                    val targetX = (position.x.toDp() - 8.dp).coerceIn(0.dp, rangeSize)
                                    val targetY = (position.y.toDp() - 8.dp).coerceIn(0.dp, rangeSize)
                                    val newSat = ratio(targetX, rangeSize)
                                    val newVal = 1f - ratio(targetY, rangeSize)
                                    updateSv(newSat, newVal)
                                }
                                updatePosition(down.position)
                                down.consume()
                                do {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.first()
                                    updatePosition(change.position)
                                    change.consume()
                                } while (event.changes.fastAny { it.pressed })
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
