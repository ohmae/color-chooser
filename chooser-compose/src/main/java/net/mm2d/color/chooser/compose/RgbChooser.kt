package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import net.mm2d.color.chooser.compose.ColorSource.HSV
import net.mm2d.color.chooser.compose.ColorSource.RGB

@Composable
fun RgbChooser(
    colorDataState: MutableState<ColorData>,
    modifier: Modifier = Modifier,
    touchCapturing: MutableState<Boolean> = mutableStateOf(false),
) {
    val colorData by colorDataState
    val color = colorData.color
    var redState = remember { mutableStateOf(color.red) }
    var greenState = remember { mutableStateOf(color.green) }
    var blueState = remember { mutableStateOf(color.blue) }

    LaunchedEffect(Unit) {
        snapshotFlow {
            Color(
                red = redState.value,
                green = greenState.value,
                blue = blueState.value,
            )
        }
            .collect {
                colorDataState.value = ColorData(it.toArgb(), RGB)
            }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { colorData }
            .collect { colorData ->
                if (colorData.source == HSV) return@collect
                val c = colorData.color
                redState.value = c.red
                greenState.value = c.green
                blueState.value = c.blue
            }
    }
    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
        ) {
            Slider(
                maxColor = Color.Red,
                valueState = redState,
                touchCapturing = touchCapturing,
            )
            Slider(
                maxColor = Color.Green,
                valueState = greenState,
                touchCapturing = touchCapturing,
            )
            Slider(
                maxColor = Color.Blue,
                valueState = blueState,
                touchCapturing = touchCapturing,
            )
        }
    }
}

private fun Color.applyValue(value: Int): Color {
    val ratio = value / 255f
    return Color(
        red = red * ratio,
        green = green * ratio,
        blue = blue * ratio,
    )
}

@Composable
fun Slider(
    modifier: Modifier = Modifier,
    maxColor: Color,
    valueState: MutableState<Int>,
    touchCapturing: MutableState<Boolean> = mutableStateOf(false),
) {
    var value by valueState
    Row(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.size(width = 256.dp + 8.dp * 2, height = 24.dp + 8.dp * 2),
        ) {
            var x by remember { mutableStateOf(value.dp) }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color(0x1a000000))
                    .padding(1.dp)
                    .background(Color.White)
                    .padding(2.dp)
                    .size(width = 256.dp, height = 24.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Black,
                                maxColor,
                            ),
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
                                    .coerceIn(0.dp, 256.dp)
                                value = x.value.toInt().coerceIn(0, 255)
                            } while (event.changes.fastAny { it.pressed })
                            touchCapturing.value = false
                        }
                    },
            )
            Box(
                modifier = Modifier
                    .padding(start = x, top = 12.dp)
                    .size(16.dp, 16.dp)
                    .clip(CircleShape)
                    .background(Color(0x1a000000))
                    .padding(1.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(maxColor.applyValue(value)),
                content = {},
            )
        }
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = LocalDensity.current.run { 12.dp.toSp() },
            ),
            textAlign = TextAlign.End,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .width(48.dp)
                .padding(horizontal = 8.dp),
        )
    }
}
