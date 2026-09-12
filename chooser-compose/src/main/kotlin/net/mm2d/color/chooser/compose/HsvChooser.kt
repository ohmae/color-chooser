/*
 * Copyright (c) 2024 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.MeshGradientPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import net.mm2d.color.chooser.compose.util.ColorControlGrip
import net.mm2d.color.chooser.compose.util.frameDecoration
import net.mm2d.color.chooser.compose.util.toHsv

@Composable
internal fun HsvChooser(
    modifier: Modifier = Modifier,
    colorEventState: MutableState<ColorEvent>,
    touchCapturing: MutableState<Boolean> = mutableStateOf(false),
) {
    val colorEvent by colorEventState
    val hsv = FloatArray(3)
    colorEvent.color.toHsv(hsv)
    val hueState = remember { mutableFloatStateOf(hsv[0]) }
    val saturationState = remember { mutableFloatStateOf(hsv[1]) }
    val valueState = remember { mutableFloatStateOf(hsv[2]) }
    LaunchedEffect(Unit) {
        snapshotFlow { colorEvent }
            .collect {
                if (it.source == ColorSource.HSV) return@collect
                if (it.source == ColorSource.ALPHA) return@collect
                it.color.toHsv(hsv)
                hueState.floatValue = hsv[0]
                saturationState.floatValue = hsv[1]
                valueState.floatValue = hsv[2]
            }
    }
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val sizeState = remember { mutableStateOf(255.dp) }
        sizeState.value = calculateSize(this.maxWidth, this.maxHeight)
        Row(
            modifier = Modifier.align(Alignment.Center),
        ) {
            SvView(
                colorEventState = colorEventState,
                hueState = hueState,
                saturationState = saturationState,
                valueState = valueState,
                touchCapturing = touchCapturing,
                sizeState = sizeState,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            HueView(
                colorEventState = colorEventState,
                hueState = hueState,
                saturationState = saturationState,
                valueState = valueState,
                touchCapturing = touchCapturing,
                sizeState = sizeState,
                modifier = Modifier
                    .align(Alignment.CenterVertically),
            )
        }
    }
}

private fun calculateSize(
    maxWidth: Dp,
    maxHeight: Dp,
): Dp = minOf(maxWidth - 8.dp * 6 - 24.dp, maxHeight - 8.dp * 2).value.toInt().dp

@Composable
private fun HueView(
    colorEventState: MutableState<ColorEvent>,
    hueState: MutableFloatState,
    saturationState: MutableFloatState,
    valueState: MutableFloatState,
    touchCapturing: MutableState<Boolean>,
    sizeState: MutableState<Dp>,
    modifier: Modifier = Modifier,
) {
    var colorEvent by colorEventState
    var hue by hueState
    val saturation by saturationState
    val value by valueState
    val size by sizeState
    var y = size * (hue / 360f)

    val brush = remember {
        val grid = 36
        Brush.verticalGradient(
            buildList {
                repeat(grid + 1) {
                    add(Color.hsv(it.toFloat() / grid * 360f, 1f, 1f))
                }
            },
        )
    }
    Box(
        modifier = modifier.size(24.dp + 8.dp * 2, size + 8.dp * 2),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .frameDecoration()
                .size(24.dp, size)
                .background(brush)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        touchCapturing.value = true
                        do {
                            val event = awaitPointerEvent()
                            y = event.changes.first().position.y
                                .toDp()
                                .coerceIn(0.dp, size)
                            hue = (y.value / size.value * 360f).coerceIn(0f, 360f)
                            colorEvent = ColorEvent(
                                Color.hsv(
                                    hue = hue,
                                    saturation = saturation,
                                    value = value,
                                    alpha = colorEvent.color.alpha,
                                ),
                                ColorSource.HSV,
                            )
                        } while (event.changes.fastAny { it.pressed })
                        touchCapturing.value = false
                    }
                },
        )
        ColorControlGrip(
            color = Color.hsv(hue = hue, saturation = 1f, value = 1f),
            modifier = Modifier.padding(start = 12.dp, top = y),
        )
    }
}

@Composable
private fun SvView(
    colorEventState: MutableState<ColorEvent>,
    hueState: MutableFloatState,
    saturationState: MutableFloatState,
    valueState: MutableFloatState,
    touchCapturing: MutableState<Boolean>,
    sizeState: MutableState<Dp>,
    modifier: Modifier = Modifier,
) {
    var colorEvent by colorEventState
    val color = colorEvent.color.copy(alpha = 1f)
    val hue by hueState
    var saturation by saturationState
    var value by valueState
    val size by sizeState

    val maxColor = Color.hsv(hue = hue, saturation = 1f, value = 1f)

    val gradientPainter = remember(maxColor) {
        val grid = 15
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
    var x = size * saturation
    var y = size - size * value

    Box(
        modifier = modifier.size(size + 8.dp * 2),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .frameDecoration()
                .size(size)
                .paint(gradientPainter)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        touchCapturing.value = true
                        do {
                            val event = awaitPointerEvent()
                            x = event.changes.first().position.x
                                .toDp()
                                .coerceIn(0.dp, size)
                            y = event.changes.first().position.y
                                .toDp()
                                .coerceIn(0.dp, size)
                            saturation = (x.value / size.value).coerceIn(0f, 1f)
                            value = ((size.value - y.value) / size.value).coerceIn(0f, 1f)
                            colorEvent = ColorEvent(
                                Color.hsv(
                                    hue = hue,
                                    saturation = saturation,
                                    value = value,
                                    alpha = colorEvent.color.alpha,
                                ),
                                ColorSource.HSV,
                            )
                        } while (event.changes.fastAny { it.pressed })
                        touchCapturing.value = false
                    }
                },
        )
        ColorControlGrip(
            color = color,
            modifier = Modifier.padding(start = x, top = y),
        )
    }
}
