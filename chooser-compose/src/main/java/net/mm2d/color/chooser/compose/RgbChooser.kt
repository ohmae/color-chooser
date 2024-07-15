package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import net.mm2d.color.chooser.compose.util.ColorControlGrip
import net.mm2d.color.chooser.compose.util.frameDecoration

@Composable
internal fun RgbChooser(
    colorEventState: MutableState<ColorEvent>,
    modifier: Modifier = Modifier,
    touchCapturing: MutableState<Boolean> = mutableStateOf(false),
) {
    var colorEvent by colorEventState
    val color = colorEvent.color
    val redState = remember { mutableFloatStateOf(color.red) }
    val greenState = remember { mutableFloatStateOf(color.green) }
    val blueState = remember { mutableFloatStateOf(color.blue) }

    LaunchedEffect(Unit) {
        snapshotFlow {
            Color(
                red = redState.floatValue,
                green = greenState.floatValue,
                blue = blueState.floatValue,
            )
        }
            .collect {
                colorEvent =
                    ColorEvent(it.copy(alpha = colorEvent.color.alpha), ColorSource.RGB)
            }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { colorEvent }
            .collect {
                if (it.source == ColorSource.RGB) return@collect
                redState.floatValue = it.color.red
                greenState.floatValue = it.color.green
                blueState.floatValue = it.color.blue
            }
    }
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val sizeState = remember { mutableStateOf(255.dp) }
        sizeState.value = calculateSize(maxWidth)
        Column(
            modifier = Modifier
                .align(Alignment.Center),
        ) {
            Slider(
                maxColor = Color.Red,
                valueState = redState,
                sizeState = sizeState,
                touchCapturing = touchCapturing,
            )
            Slider(
                maxColor = Color.Green,
                valueState = greenState,
                sizeState = sizeState,
                touchCapturing = touchCapturing,
            )
            Slider(
                maxColor = Color.Blue,
                valueState = blueState,
                sizeState = sizeState,
                touchCapturing = touchCapturing,
            )
        }
    }
}

private fun calculateSize(maxWidth: Dp): Dp =
    (maxWidth - 8.dp * 6 - 42.dp).value.toInt().dp

@Composable
private fun Slider(
    maxColor: Color,
    valueState: MutableFloatState,
    sizeState: MutableState<Dp>,
    modifier: Modifier = Modifier,
    touchCapturing: MutableState<Boolean> = mutableStateOf(false),
) {
    var value by valueState
    val size by sizeState
    var x = size * value

    Row(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.size(width = size + 8.dp * 2, height = 32.dp + 8.dp * 2),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .frameDecoration()
                    .size(width = size, height = 32.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Black, maxColor),
                        ),
                    )
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown()
                            touchCapturing.value = true
                            do {
                                val event = awaitPointerEvent()
                                x = event.changes.first().position.x
                                    .toDp()
                                    .coerceIn(0.dp, size)
                                value = (x.value / size.value).coerceIn(0f, 1f)
                            } while (event.changes.fastAny { it.pressed })
                            touchCapturing.value = false
                        }
                    },
            )
            ColorControlGrip(
                color = maxColor.applyValue(value),
                modifier = Modifier.padding(start = x, top = 16.dp),
            )
        }
        Text(
            text = (value * 255f).toInt().toString(),
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = LocalDensity.current.run { 12.dp.toSp() },
            ),
            textAlign = TextAlign.End,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .width(42.dp)
                .padding(horizontal = 8.dp),
        )
    }
}

private fun Color.applyValue(ratio: Float): Color =
    Color(
        red = red * ratio,
        green = green * ratio,
        blue = blue * ratio,
    )
