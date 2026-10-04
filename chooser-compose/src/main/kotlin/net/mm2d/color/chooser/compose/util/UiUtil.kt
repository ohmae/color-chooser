/*
 * Copyright (c) 2024 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose.util

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
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

// 押下位置を即時反映し、横ドラッグで更新する。縦ドラッグは親のスクロールに渡す。
internal suspend fun PointerInputScope.detectHorizontalTapAndDragGestures(
    onPositionChange: (Offset) -> Unit,
) {
    coroutineScope {
        // pointerInputが最初のdownイベントをディスパッチする前に、両方の検出器を登録
        launch(start = CoroutineStart.UNDISPATCHED) {
            detectTapGestures(onPress = { onPositionChange(it) })
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

internal val TRACK_HEIGHT: Dp = 32.dp
internal val CONTROL_GRIP_RADIUS: Dp = 8.dp

@Composable
internal fun SliderGrip(
    color: Color,
    modifier: Modifier = Modifier,
    active: Boolean = false,
) {
    val pillHeight by animateDpAsState(
        targetValue = if (active) 44.dp else 38.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "sliderGripHeight",
    )
    val pillWidth by animateDpAsState(
        targetValue = if (active) 12.dp else 10.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "sliderGripWidth",
    )
    val emphasis by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        label = "sliderGripEmphasis",
    )

    Box(
        modifier = modifier
            .size(width = 16.dp, height = TRACK_HEIGHT)
            .drawBehind {
                val widthPx = pillWidth.toPx()
                val heightPx = pillHeight.toPx()
                val left = (size.width - widthPx) / 2f
                val top = (size.height - heightPx) / 2f
                val stroke1 = (1 + emphasis).dp.toPx()
                val stroke2 = 1.dp.toPx()

                // 外側ボーダー（黒半透明）
                drawRoundRect(
                    color = colorBorder2,
                    topLeft = Offset(left, top),
                    size = Size(widthPx, heightPx),
                    cornerRadius = CornerRadius(widthPx / 2f, widthPx / 2f),
                )
                // 内側ボーダー（白）
                val w1 = (widthPx - stroke1 * 2f).coerceAtLeast(0f)
                val h1 = (heightPx - stroke1 * 2f).coerceAtLeast(0f)
                drawRoundRect(
                    color = colorBorder1,
                    topLeft = Offset(left + stroke1, top + stroke1),
                    size = Size(w1, h1),
                    cornerRadius = CornerRadius(w1 / 2f, w1 / 2f),
                )
                // 塗り（選択色）
                val totalStroke = stroke1 + stroke2
                val w2 = (widthPx - totalStroke * 2f).coerceAtLeast(0f)
                val h2 = (heightPx - totalStroke * 2f).coerceAtLeast(0f)
                drawRoundRect(
                    color = color,
                    topLeft = Offset(left + totalStroke, top + totalStroke),
                    size = Size(w2, h2),
                    cornerRadius = CornerRadius(w2 / 2f, w2 / 2f),
                )
            },
    )
}

@Composable
internal fun SvGrip(
    color: Color,
    modifier: Modifier = Modifier,
    active: Boolean = false,
) {
    val tickLength by animateDpAsState(
        targetValue = if (active) 24.dp else 8.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "svTickLength",
    )
    val emphasis by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        label = "svEmphasis",
    )

    Box(
        modifier = modifier
            .size(16.dp)
            .drawBehind {
                val center = Offset(size.width / 2f, size.height / 2f)
                val ringRadiusPx = 10.dp.toPx()
                val tickLengthPx = tickLength.toPx()
                val tickGapPx = 2.dp.toPx()
                val stroke1 = (1 + emphasis).dp.toPx()
                val stroke2 = 1.dp.toPx()

                // 1. 十字照準線（Ticks）
                val startRadiusPx = ringRadiusPx + tickGapPx
                val tickStarts = listOf(
                    Offset(center.x, center.y - startRadiusPx),
                    Offset(center.x, center.y + startRadiusPx),
                    Offset(center.x - startRadiusPx, center.y),
                    Offset(center.x + startRadiusPx, center.y),
                )
                val endRadiusPx = startRadiusPx + tickLengthPx
                val tickEnds = listOf(
                    Offset(center.x, center.y - endRadiusPx),
                    Offset(center.x, center.y + endRadiusPx),
                    Offset(center.x - endRadiusPx, center.y),
                    Offset(center.x + endRadiusPx, center.y),
                )
                for (i in tickStarts.indices) {
                    drawLine(
                        color = colorBorder2,
                        start = tickStarts[i],
                        end = tickEnds[i],
                        strokeWidth = (2f + emphasis).dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = colorBorder1,
                        start = tickStarts[i],
                        end = tickEnds[i],
                        strokeWidth = 1.2.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }

                // 2. 中心ドット（Center Dot）
                val dotRadius = 8.dp.toPx()
                drawCircle(color = colorBorder2, radius = dotRadius, center = center)
                drawCircle(color = colorBorder1, radius = (dotRadius - stroke1).coerceAtLeast(0f), center = center)
                drawCircle(color = color, radius = (dotRadius - stroke1 - stroke2).coerceAtLeast(0f), center = center)
            },
    )
}

internal fun ratio(
    target: Float,
    range: Float,
): Float =
    if (range > 0f) {
        (target / range).coerceIn(0f, 1f)
    } else {
        0f
    }
