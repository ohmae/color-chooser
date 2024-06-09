package net.mm2d.color.chooser.compose

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import net.mm2d.color.chooser.compose.ColorSource.HSV

@Composable
fun HsvChooser(
    modifier: Modifier = Modifier,
    opacityEventState: MutableState<ColorEvent>,
    touchCapturing: MutableState<Boolean> = mutableStateOf(false),
) {
    val opacityEvent by opacityEventState
    val hsv = FloatArray(3)
    ColorUtils.colorToHsv(opacityEvent.color, hsv)
    val hueState = remember { mutableStateOf(hsv[0]) }
    val saturationState = remember { mutableStateOf(hsv[1]) }
    val valueState = remember { mutableStateOf(hsv[2]) }
    LaunchedEffect(Unit) {
        snapshotFlow { opacityEvent }
            .collect {
                if (it.source == HSV) return@collect
                val c = it.color
                ColorUtils.colorToHsv(c, hsv)
                hueState.value = hsv[0]
                saturationState.value = hsv[1]
                valueState.value = hsv[2]
            }
    }
    Box(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
        ) {
            SvView(
                opacityEventState = opacityEventState,
                hueState = hueState,
                saturationState = saturationState,
                valueState = valueState,
                touchCapturing = touchCapturing,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            HueView(
                opacityEventState = opacityEventState,
                hueState = hueState,
                saturationState = saturationState,
                valueState = valueState,
                touchCapturing = touchCapturing,
                modifier = Modifier
                    .align(Alignment.CenterVertically),
            )
        }
    }
}

@Composable
fun HueView(
    opacityEventState: MutableState<ColorEvent>,
    hueState: MutableState<Float>,
    saturationState: MutableState<Float>,
    valueState: MutableState<Float>,
    touchCapturing: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    var opacityEvent by opacityEventState
    var hue by hueState
    val saturation by saturationState
    val value by valueState
    var y by remember { mutableStateOf(0.dp) }

    LaunchedEffect(Unit) {
        snapshotFlow { hue }
            .collect {
                y = (it * 255f).dp
            }
    }

    Box(
        modifier = modifier.size(24.dp + 8.dp * 2, 255.dp + 8.dp * 2),
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color(0x1a000000))
                .padding(1.dp)
                .background(Color.White)
                .padding(2.dp)
                .size(24.dp, 255.dp)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        touchCapturing.value = false
                        do {
                            val event = awaitPointerEvent()
                            y = event.changes.first().position.y
                                .toDp()
                                .coerceIn(0.dp, 255.dp)
                            hue = (y.value / 255f).coerceIn(0f, 1f)
                            opacityEvent = ColorEvent(
                                ColorUtils.hsvToColor(hue, saturation, value),
                                HSV,
                            )
                        } while (event.changes.fastAny { it.pressed })
                        touchCapturing.value = true
                    }
                },
            bitmap = hueBitmap.asImageBitmap(),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
        )
        Box(
            modifier = Modifier
                .padding(start = 12.dp, top = y)
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(0x1a000000))
                .padding(1.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(2.dp)
                .clip(CircleShape)
                .background(Color(ColorUtils.hsvToColor(hue, 1f, 1f))),
            content = {},
        )
    }
}

@Composable
fun SvView(
    opacityEventState: MutableState<ColorEvent>,
    hueState: MutableState<Float>,
    saturationState: MutableState<Float>,
    valueState: MutableState<Float>,
    touchCapturing: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    var opacityEvent by opacityEventState
    val hue by hueState
    var saturation by saturationState
    var value by valueState

    var maxColor by remember { mutableStateOf(ColorUtils.hsvToColor(hue, 1f, 1f)) }
    var x by remember { mutableStateOf((saturation * 255f).dp) }
    var y by remember { mutableStateOf((255f - value * 255f).dp) }

    LaunchedEffect(Unit) {
        snapshotFlow { opacityEvent }
            .collect {
                if (it.source == HSV) return@collect
                maxColor = ColorUtils.hsvToColor(hue, 1f, 1f)
                x = (saturation * 255f).dp
                y = (255f - value * 255f).dp
            }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { hue }
            .collect {
                maxColor = ColorUtils.hsvToColor(it, 1f, 1f)
            }
    }

    Box(
        modifier = modifier.size(255.dp + 8.dp * 2),
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color(0x1a000000))
                .padding(1.dp)
                .background(Color.White)
                .padding(2.dp)
                .background(Color(maxColor))
                .size(255.dp)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        touchCapturing.value = false
                        do {
                            val event = awaitPointerEvent()
                            x = event.changes.first().position.x
                                .toDp()
                                .coerceIn(0.dp, 255.dp)
                            y = event.changes.first().position.y
                                .toDp()
                                .coerceIn(0.dp, 255.dp)
                            saturation = (x.value / 255f).coerceIn(0f, 1f)
                            value = ((255f - y.value) / 255f).coerceIn(0f, 1f)
                            opacityEvent = ColorEvent(
                                ColorUtils.hsvToColor(hue, saturation, value),
                                HSV,
                            )
                        } while (event.changes.fastAny { it.pressed })
                        touchCapturing.value = true
                    }
                },
            bitmap = maskBitmap.asImageBitmap(),
            contentDescription = null,
        )
        Box(
            modifier = Modifier
                .padding(top = y, start = x)
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(0x1a000000))
                .padding(1.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(2.dp)
                .clip(CircleShape)
                .background(Color(opacityEvent.color)),
            content = {},
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
            pixels[x + y * TONE_SIZE] = ColorUtils.svToMask(x / TONE_MAX, (TONE_MAX - y) / TONE_MAX)
        }
    }
    return Bitmap.createBitmap(pixels, TONE_SIZE, TONE_SIZE, Bitmap.Config.ARGB_8888)
}

private fun createHueBitmap(): Bitmap {
    val pixels = IntArray(RANGE) { ColorUtils.hsvToColor(it.toFloat() / RANGE, 1f, 1f) }
    return Bitmap.createBitmap(pixels, 1, RANGE, Bitmap.Config.ARGB_8888)
}
