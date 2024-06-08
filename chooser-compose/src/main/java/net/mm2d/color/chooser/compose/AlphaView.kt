package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny

@Composable
fun AlphaView(
    colorState: MutableState<Int>,
    alphaState: MutableState<Int>,
    modifier: Modifier = Modifier,
) {
    val color by colorState
    var alpha by alphaState
    Row(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.size(width = 256.dp + 8.dp * 2, height = 24.dp + 8.dp * 2),
        ) {
            var x by remember { mutableStateOf(alpha.dp) }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color(0x1a000000))
                    .padding(1.dp)
                    .background(Color.White)
                    .padding(2.dp)
                    .size(width = 256.dp, height = 24.dp)
                    .background(
                        ShaderBrush(
                            ImageShader(
                                ImageBitmap.imageResource(id = R.drawable.mm2d_cc_bg_alpha),
                                TileMode.Repeated,
                                TileMode.Repeated,
                            ),
                        ),
                    )
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color(color.toOpacity()),
                            ),
                        ),
                    )
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown()
                            do {
                                val event = awaitPointerEvent()
                                x = event.changes.first().position.x
                                    .toDp()
                                    .coerceIn(0.dp, 256.dp)
                                alpha = x.value.toInt().coerceIn(0, 255)
                            } while (event.changes.fastAny { it.pressed })
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
                    .background(Color(color)),
                content = {},
            )
        }
        Text(
            text = alpha.toString(),
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
