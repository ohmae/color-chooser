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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.R

private val colorBorder1 = Color.White
private val colorBorder2 = Color(0x1a000000)

internal fun Modifier.frameDecoration(): Modifier =
    this
        .background(colorBorder2)
        .padding(1.dp)
        .background(colorBorder1)
        .padding(2.dp)

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

internal suspend fun PointerInputScope.detectTapAndDragGestures(
    onPositionChange: (Offset) -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        onPositionChange(down.position)
        down.consume()
        val pointerId = down.id
        while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == pointerId } ?: break
            if (!change.pressed) break
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
            .size(16.dp, 16.dp)
            .clip(CircleShape)
            .background(colorBorder2)
            .padding(1.dp)
            .clip(CircleShape)
            .background(colorBorder1)
            .padding(2.dp)
            .clip(CircleShape)
            .background(color),
    )
}

internal val CONTROL_GRIP_RADIUS: Dp = 8.dp

internal fun ratio(
    target: Float,
    range: Float,
): Float =
    if (range >= 0f) {
        (target / range).coerceIn(0f, 1f)
    } else {
        0f
    }
