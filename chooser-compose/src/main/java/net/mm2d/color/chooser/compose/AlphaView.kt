package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import net.mm2d.color.chooser.compose.util.alphaBackgroundBrush
import net.mm2d.color.chooser.compose.util.frameDecoration

@Composable
internal fun AlphaView(
    colorEventState: MutableState<ColorEvent>,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val sizeState = remember { mutableStateOf(255.dp) }
        sizeState.value = calculateSize(maxWidth)
        Box(modifier = Modifier.align(Alignment.Center)) {
            Slider(
                colorEventState = colorEventState,
                sizeState = sizeState,
            )
        }
    }
}

private fun calculateSize(maxWidth: Dp): Dp =
    (maxWidth - 8.dp * 6 - 42.dp).value.toInt().dp

@Composable
private fun Slider(
    colorEventState: MutableState<ColorEvent>,
    sizeState: MutableState<Dp>,
    modifier: Modifier = Modifier,
) {
    var colorEvent by colorEventState
    val size by sizeState
    val opacity = colorEvent.color.copy(alpha = 1f)
    var alpha = colorEvent.color.alpha
    var x = size * alpha

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
                    .background(alphaBackgroundBrush())
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, opacity),
                        ),
                    )
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown()
                            do {
                                val event = awaitPointerEvent()
                                x = event.changes.first().position.x
                                    .toDp()
                                    .coerceIn(0.dp, size)
                                alpha = (x.value / size.value).coerceIn(0f, 1f)
                                colorEvent = ColorEvent(
                                    colorEvent.color.copy(alpha = alpha),
                                    ColorSource.ALPHA,
                                )
                            } while (event.changes.fastAny { it.pressed })
                        }
                    },
            )
            ColorControlGrip(
                color = opacity,
                modifier = Modifier
                    .padding(start = x, top = 16.dp),
            )
        }
        Text(
            text = (alpha * 255f).toInt().toString(),
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
