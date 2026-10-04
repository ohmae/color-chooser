/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.util.CONTROL_GRIP_RADIUS
import net.mm2d.color.chooser.compose.util.ChooserShapes
import net.mm2d.color.chooser.compose.util.ControlInteraction
import net.mm2d.color.chooser.compose.util.SliderGrip
import net.mm2d.color.chooser.compose.util.TRACK_HEIGHT
import net.mm2d.color.chooser.compose.util.alphaBackgroundBrush
import net.mm2d.color.chooser.compose.util.controlInteraction
import net.mm2d.color.chooser.compose.util.detectHorizontalTapAndDragGestures
import net.mm2d.color.chooser.compose.util.frameDecoration
import net.mm2d.color.chooser.compose.util.ratio
import kotlin.math.roundToInt

private const val MAX_INT = 255
private val RANGE_INT = 0..MAX_INT
private const val MAX_FLOAT = MAX_INT.toFloat()
private val RANGE_FLOAT = 0f..MAX_FLOAT

// スライダートラック本体（グラデーション描画、グリップ、タッチ・キーボード操作、アクセシビリティ）
@Composable
internal fun ColorSliderTrack(
    value: Int,
    onValueChange: (Int) -> Unit,
    color: Color,
    accessibilityLabel: String,
    modifier: Modifier = Modifier,
    alphaMode: Boolean = false,
    interaction: ControlInteraction = remember { ControlInteraction() },
) {
    val currentOnValueChanged by rememberUpdatedState(onValueChange)
    val density = LocalDensity.current
    val gripRadiusPx = remember(density) {
        with(density) { CONTROL_GRIP_RADIUS.roundToPx() }
    }
    var trackWidthPx by remember { mutableIntStateOf(0) }
    val currentRatio = (value / 255f).coerceIn(0f, 1f)

    val colorBrush = remember(color, alphaMode) {
        val colors = if (alphaMode) {
            listOf(color.copy(alpha = 0f), color)
        } else {
            listOf(Color.Black, color)
        }
        Brush.horizontalGradient(colors)
    }
    val gripColor = remember(color, alphaMode, currentRatio) {
        if (alphaMode) {
            color.copy(alpha = currentRatio)
        } else {
            lerp(Color.Black, color, currentRatio)
        }
    }

    Box(
        modifier = modifier
            .height(TRACK_HEIGHT)
            .onSizeChanged { trackWidthPx = it.width },
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 6.dp)
                .fillMaxSize()
                .frameDecoration(ChooserShapes.track)
                .then(if (alphaMode) Modifier.background(alphaBackgroundBrush()) else Modifier)
                .background(colorBrush),
        )
        SliderGrip(
            active = interaction.active,
            color = gripColor,
            modifier = Modifier
                .align(AbsoluteAlignment.TopLeft)
                .absoluteOffset {
                    val rangeXPx = (trackWidthPx - gripRadiusPx * 2).coerceAtLeast(0)
                    val x = (rangeXPx * currentRatio).roundToInt()
                    IntOffset(x = x, y = 0)
                },
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .controlInteraction(interaction)
                .accessibility(accessibilityLabel, value, currentOnValueChanged)
                .pointerInput(trackWidthPx, density) {
                    if (trackWidthPx <= 0) return@pointerInput
                    val rangeXPx = (trackWidthPx - gripRadiusPx * 2).coerceAtLeast(0)
                    if (rangeXPx == 0) return@pointerInput
                    detectHorizontalTapAndDragGestures { position ->
                        val targetX = position.x - gripRadiusPx
                        val ratio = ratio(targetX, rangeXPx.toFloat())
                        currentOnValueChanged((ratio * MAX_FLOAT).roundToInt())
                    }
                },
        )
    }
}

// 単一行スライダー（アルファ用、およびテスト用）
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
    val interaction = remember { ControlInteraction() }
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val valueWidth = remember(textMeasurer, density, labelStyle) {
        with(density) {
            textMeasurer.measure(text = "255", style = labelStyle, maxLines = 1).size.width.toDp()
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ColorSliderTrack(
            value = value,
            onValueChange = onValueChange,
            color = color,
            accessibilityLabel = accessibilityLabel,
            modifier = Modifier.weight(1f),
            alphaMode = alphaMode,
            interaction = interaction,
        )
        Text(
            text = value.toString(),
            style = labelStyle,
            color = labelColor,
            textAlign = TextAlign.End,
            softWrap = false,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 6.dp)
                .width(valueWidth),
        )
    }
}

// 2段式エクスプレッシブRGBスライダー（上段：ラベル＋ピル型数値バッジ、下段：フル幅トラック）
@Composable
internal fun RgbSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
    labelColor: Color,
    labelStyle: TextStyle,
) {
    val interaction = remember { ControlInteraction() }
    val badgeScale by animateFloatAsState(
        targetValue = if (interaction.active) 1.06f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "rgbBadgeScale",
    )
    val badgeBgColor by animateColorAsState(
        targetValue = if (interaction.active) {
            color.copy(alpha = 0.20f)
        } else {
            color.copy(alpha = 0.10f)
        },
        label = "rgbBadgeBgColor",
    )
    val badgeBorderColor by animateColorAsState(
        targetValue = if (interaction.active) {
            color.copy(alpha = 0.55f)
        } else {
            color.copy(alpha = 0.25f)
        },
        label = "rgbBadgeBorderColor",
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(color, CircleShape)
                        .border(1.dp, Color.Black.copy(alpha = 0.2f), CircleShape),
                )
                Text(
                    text = label,
                    style = labelStyle,
                    color = labelColor,
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = badgeScale
                        scaleY = badgeScale
                    }
                    .heightIn(min = 24.dp)
                    .widthIn(min = 44.dp)
                    .background(badgeBgColor, ChooserShapes.label)
                    .border(1.dp, badgeBorderColor, ChooserShapes.label)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = value.toString(),
                    style = labelStyle,
                    color = labelColor,
                    softWrap = false,
                    textAlign = TextAlign.Center,
                )
            }
        }
        ColorSliderTrack(
            value = value,
            onValueChange = onValueChange,
            color = color,
            accessibilityLabel = label,
            alphaMode = false,
            interaction = interaction,
            modifier = Modifier.fillMaxWidth(),
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

@PreviewLightDark
@Composable
private fun PreviewColorSlider() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
    ) {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ColorSlider(
                    value = 128,
                    onValueChange = {},
                    color = Color.Red,
                    accessibilityLabel = "Red",
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    labelStyle = ColorChooserDefaults.sliderLabelStyle,
                )
                ColorSlider(
                    value = 128,
                    onValueChange = {},
                    color = Color.Blue,
                    accessibilityLabel = "Alpha",
                    alphaMode = true,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    labelStyle = ColorChooserDefaults.sliderLabelStyle,
                )
                RgbSlider(
                    value = 220,
                    onValueChange = {},
                    color = Color.Red,
                    label = "Red",
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    labelStyle = ColorChooserDefaults.sliderLabelStyle,
                )
            }
        }
    }
}
