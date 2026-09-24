/*
 * Copyright (c) 2024 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose.util

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.mm2d.color.chooser.compose.R

private val colorBorder1 = Color.White
private val colorBorder2 = Color(0x1a000000)

internal fun Modifier.frameDecoration(
    shape: Shape = RectangleShape,
): Modifier =
    this
        .background(colorBorder2, shape)
        .padding(1.dp)
        .background(colorBorder1, shape)
        .padding(1.dp)
        .clip(shape)

@Composable
internal fun alphaBackgroundBrush(): ShaderBrush {
    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.mm2d_cc_bg_alpha)
    return remember(imageBitmap) {
        ShaderBrush(
            ImageShader(
                imageBitmap,
                TileMode.Repeated,
                TileMode.Repeated,
            ),
        )
    }
}

// A vertical scroll can cancel the pending tap before it changes the color.
internal suspend fun PointerInputScope.detectHorizontalTapAndDragGestures(
    onPositionChange: (Offset) -> Unit,
) {
    coroutineScope {
        // Register both detectors before pointerInput dispatches its first down event.
        launch(start = CoroutineStart.UNDISPATCHED) {
            detectTapGestures(onTap = onPositionChange)
        }
        launch(start = CoroutineStart.UNDISPATCHED) {
            detectHorizontalDragGestures { change, _ ->
                onPositionChange(change.position)
            }
        }
    }
}

// The saturation/value plane owns drags in both directions, starting at the press position.
internal suspend fun PointerInputScope.detectTapAndDragGestures(
    onPositionChange: (Offset) -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown()
        onPositionChange(down.position)
        down.consume()
        drag(down.id) { change ->
            onPositionChange(change.position)
            change.consume()
        }
    }
}

@Composable
internal fun ControlGrip(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(16.dp)
            .drawBehind {
                val radius = size.minDimension / 2f
                val stroke1 = 1.dp.toPx()
                val stroke2 = 2.dp.toPx()
                drawCircle(color = colorBorder2, radius = radius)
                drawCircle(color = colorBorder1, radius = (radius - stroke1).coerceAtLeast(0f))
                drawCircle(color = color, radius = (radius - stroke1 - stroke2).coerceAtLeast(0f))
            },
    )
}

internal val CONTROL_GRIP_RADIUS: Dp = 8.dp

internal fun ratio(
    target: Float,
    range: Float,
): Float =
    if (range > 0f) {
        (target / range).coerceIn(0f, 1f)
    } else {
        0f
    }
