package net.mm2d.color.chooser.compose

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
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
        sizeState.value = calculateSize(maxWidth, maxHeight)
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

private fun calculateSize(maxWidth: Dp, maxHeight: Dp): Dp =
    minOf(maxWidth - 8.dp * 6 - 24.dp, maxHeight - 8.dp * 2).value.toInt().dp

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

    Box(
        modifier = modifier.size(24.dp + 8.dp * 2, size + 8.dp * 2),
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .frameDecoration()
                .size(24.dp, size)
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
            bitmap = hueBitmap.asImageBitmap(),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
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

    var x by remember { mutableStateOf(0.dp) }
    var y by remember { mutableStateOf(0.dp) }

    val s = (x.value / size.value).coerceIn(0f, 1f)
    val v = ((size.value - y.value) / size.value).coerceIn(0f, 1f)
    if (Color.hsv(hue = hue, saturation = s, value = v) != color) {
        x = size * saturation
        y = size - size * value
    }

    Box(
        modifier = modifier.size(size + 8.dp * 2),
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .frameDecoration()
                .background(maxColor)
                .size(size)
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
            bitmap = maskBitmap.asImageBitmap(),
            contentDescription = null,
        )
        ColorControlGrip(
            color = color,
            modifier = Modifier.padding(start = x, top = y),
        )
    }
}

private val maskBitmap = createMaskBitmap()
private val hueBitmap = createHueBitmap()

private const val TONE_MAX = 255f
private const val TONE_SIZE = 255
private const val RANGE = 360

private fun createMaskBitmap(): Bitmap {
    val pixels = IntArray(TONE_SIZE * TONE_SIZE)
    repeat(TONE_SIZE) { y ->
        repeat(TONE_SIZE) { x ->
            pixels[x + y * TONE_SIZE] = svToMask(x / TONE_MAX, (TONE_MAX - y) / TONE_MAX)
        }
    }
    return Bitmap.createBitmap(pixels, TONE_SIZE, TONE_SIZE, Bitmap.Config.ARGB_8888)
}

private fun svToMask(s: Float, v: Float): Int {
    val a = 1f - (s * v)
    val g = if (a == 0f) 0f else (v * (1f - s) / a).coerceIn(0f, 1f)
    return Color(red = g, blue = g, green = g, alpha = a).toArgb()
}

private fun createHueBitmap(): Bitmap {
    val pixels = IntArray(RANGE) { Color.hsv(it.toFloat() / RANGE * 360f, 1f, 1f).toArgb() }
    return Bitmap.createBitmap(pixels, 1, RANGE, Bitmap.Config.ARGB_8888)
}
