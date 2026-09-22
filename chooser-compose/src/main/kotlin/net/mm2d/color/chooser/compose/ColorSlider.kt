/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import net.mm2d.color.chooser.compose.util.ColorControlGrip
import net.mm2d.color.chooser.compose.util.alphaBackgroundBrush
import net.mm2d.color.chooser.compose.util.frameDecoration
import net.mm2d.color.chooser.compose.util.ratio
import kotlin.math.roundToInt

private const val MAX_INT = 255
private val RANGE_INT = 0..MAX_INT
private const val MAX_FLOAT = MAX_INT.toFloat()
private val RANGE_FLOAT = 0f..MAX_FLOAT

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun ColorSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    color: Color,
    accessibilityLabel: String,
    modifier: Modifier = Modifier,
    alphaMode: Boolean = false,
    labelColor: Color,
    labelStyle: TextStyle,
) {
    val currentOnValueChanged by rememberUpdatedState(onValueChange)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .height(32.dp)
                .weight(1f),
        ) {
            val rangeX = (maxWidth - 16.dp).coerceAtLeast(0.dp)
            val currentRatio = (value / 255f).coerceIn(0f, 1f)
            val gripStartX = rangeX * currentRatio
            val colorBrush = remember(color, alphaMode) {
                if (alphaMode) {
                    Brush.horizontalGradient(
                        listOf(color.copy(alpha = 0f), color.copy(alpha = 1f)),
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(Color.Black, color),
                    )
                }
            }
            val gripColor = remember(color, alphaMode, currentRatio) {
                if (alphaMode) {
                    color.copy(alpha = currentRatio)
                } else {
                    lerp(Color.Black, color, currentRatio)
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 5.dp)
                    .frameDecoration()
                    .fillMaxSize()
                    .then(if (alphaMode) Modifier.background(alphaBackgroundBrush()) else Modifier)
                    .background(colorBrush),
            )
            ColorControlGrip(
                color = gripColor,
                modifier = Modifier
                    .align(AbsoluteAlignment.TopLeft)
                    .absoluteOffset(x = gripStartX, y = 8.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .accessibility(accessibilityLabel, value, currentOnValueChanged)
                    .pointerInput(rangeX) {
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            val updateValue = { positionX: Float ->
                                val targetX = (positionX.toDp() - 8.dp).coerceIn(0.dp, rangeX)
                                val ratio = ratio(targetX, rangeX)
                                currentOnValueChanged((ratio * MAX_FLOAT).roundToInt().coerceIn(RANGE_INT))
                            }
                            updateValue(down.position.x)
                            down.consume()
                            do {
                                val event = awaitPointerEvent()
                                val change = event.changes.first()
                                updateValue(change.position.x)
                                change.consume()
                            } while (event.changes.fastAny { it.pressed })
                        }
                    },
            )
        }
        Text(
            text = value.toString(),
            style = labelStyle,
            color = labelColor,
            textAlign = TextAlign.End,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .width(28.dp),
        )
    }
}

private fun Modifier.accessibility(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
): Modifier {
    val updateValue = { requested: Float ->
        if (!requested.isFinite()) {
            false
        } else {
            val newValue = requested.roundToInt().coerceIn(RANGE_INT)
            if (newValue == value) {
                false
            } else {
                onValueChange(newValue)
                true
            }
        }
    }
    return this
        .semantics {
            contentDescription = label
            progressBarRangeInfo = ProgressBarRangeInfo(value.toFloat(), RANGE_FLOAT, MAX_INT - 1)
            setProgress(action = updateValue)
        }
        .onKeyEvent {
            if (it.type != KeyEventType.KeyDown) return@onKeyEvent false
            val target = when (it.key) {
                Key.DirectionRight, Key.DirectionUp -> value + 1f
                Key.DirectionLeft, Key.DirectionDown -> value - 1f
                Key.MoveHome -> 0f
                Key.MoveEnd -> MAX_FLOAT
                else -> return@onKeyEvent false
            }
            updateValue(target)
            true
        }
        .focusable()
}
