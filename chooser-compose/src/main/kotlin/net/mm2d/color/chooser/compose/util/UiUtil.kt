/*
 * Copyright (c) 2024 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.mm2d.color.chooser.compose.R

private val colorBorder1 = Color.White
private val colorBorder2 = Color.Black.copy(alpha = 0.45f)

internal object ChooserShapes {
    val preview = RoundedCornerShape(24.dp)
    val label = RoundedCornerShape(percent = 50)
    val track = RoundedCornerShape(percent = 50)
}

internal class ControlInteraction {
    var pressed by mutableStateOf(false)
    var focused by mutableStateOf(false)
    val active: Boolean get() = pressed || focused
}

// プレス操作を消費したり、既存のドラッグ／スクロール検出機能と競合したりすることなく、操作を監視
internal fun Modifier.controlInteraction(
    interaction: ControlInteraction,
): Modifier =
    this
        .onFocusChanged { interaction.focused = it.isFocused }
        .pointerInput(interaction) {
            try {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    interaction.pressed = true
                    do {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                    } while (event.changes.any { it.pressed })
                    interaction.pressed = false
                }
            } finally {
                interaction.pressed = false
            }
        }

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

// 垂直スクロールを行うと、色が変わる前に保留中のタップをキャンセルする
internal suspend fun PointerInputScope.detectHorizontalTapAndDragGestures(
    onPositionChange: (Offset) -> Unit,
) {
    coroutineScope {
        // pointerInputが最初のdownイベントをディスパッチする前に、両方の検出器を登録
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

// SV面では、押下位置を起点として、上下左右方向へのドラッグ操作が可能
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
    active: Boolean = false,
) {
    val emphasis by animateFloatAsState(if (active) 1f else 0f, label = "controlEmphasis")
    Box(
        modifier = modifier
            .size(16.dp)
            .drawBehind {
                val radius = size.minDimension / 2f
                val stroke1 = (1 + emphasis).dp.toPx()
                val stroke2 = 1.dp.toPx()
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
