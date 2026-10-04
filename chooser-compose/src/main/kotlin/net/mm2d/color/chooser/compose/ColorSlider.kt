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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.util.ChooserShapes
import net.mm2d.color.chooser.compose.util.ControlInteraction
import net.mm2d.color.chooser.compose.util.alphaBackgroundBrush
import kotlin.math.roundToInt

private const val MAX_INT = 255
private const val MAX_FLOAT = MAX_INT.toFloat()

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
    content: (@Composable BoxScope.() -> Unit)? = null,
) {
    val currentOnValueChanged by rememberUpdatedState(onValueChange)
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

    HorizontalSliderTrack(
        currentRatio = currentRatio,
        onRatioChange = { currentOnValueChanged((it * MAX_FLOAT).roundToInt()) },
        colorBrush = colorBrush,
        gripColor = gripColor,
        modifier = modifier,
        accessibilityModifier = Modifier.sliderAccessibility(
            label = accessibilityLabel,
            value = value.toFloat(),
            maxValue = MAX_FLOAT,
            steps = MAX_INT - 1,
            onValueChange = { currentOnValueChanged(it.roundToInt()) },
            normalizeValue = { it.roundToInt().toFloat() },
        ),
        interaction = interaction,
        trackBackground = if (alphaMode) Modifier.background(alphaBackgroundBrush()) else Modifier,
        content = content,
    )
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
                    .widthIn(min = 56.dp)
                    .background(badgeBgColor, ChooserShapes.label)
                    .border(1.dp, badgeBorderColor, ChooserShapes.label)
                    .padding(horizontal = 12.dp, vertical = 2.dp),
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

// トラック内包型エクスプレッシブ・アルファスライダー（重なり回避機能付き）
@Composable
internal fun AlphaSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    color: Color,
    accessibilityLabel: String,
    modifier: Modifier = Modifier,
    labelColor: Color = Color.White,
    labelBackgroundColor: Color = Color.Black.copy(alpha = 0.75f),
    labelStyle: TextStyle = ColorChooserDefaults.previewLabelStyle,
) {
    val interaction = remember { ControlInteraction() }
    val isEvading = value >= 180

    val rightAlpha by animateFloatAsState(
        targetValue = if (isEvading) 0f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "alphaRightAlpha",
    )
    val rightScale by animateFloatAsState(
        targetValue = when {
            isEvading -> 0.75f
            interaction.active -> 1.08f
            else -> 1.0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "alphaRightScale",
    )
    val leftAlpha by animateFloatAsState(
        targetValue = if (isEvading) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "alphaLeftAlpha",
    )
    val leftScale by animateFloatAsState(
        targetValue = when {
            !isEvading -> 0.75f
            interaction.active -> 1.08f
            else -> 1.0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "alphaLeftScale",
    )

    ColorSliderTrack(
        value = value,
        onValueChange = onValueChange,
        color = color,
        accessibilityLabel = accessibilityLabel,
        alphaMode = true,
        interaction = interaction,
        modifier = modifier.fillMaxWidth(),
    ) {
        if (leftAlpha > 0.01f) {
            AlphaBadge(
                value = value,
                scale = leftScale,
                alpha = leftAlpha,
                backgroundColor = labelBackgroundColor,
                contentColor = labelColor,
                labelStyle = labelStyle,
                modifier = Modifier
                    .align(AbsoluteAlignment.CenterLeft)
                    .absolutePadding(left = 12.dp),
            )
        }
        if (rightAlpha > 0.01f) {
            AlphaBadge(
                value = value,
                scale = rightScale,
                alpha = rightAlpha,
                backgroundColor = labelBackgroundColor,
                contentColor = labelColor,
                labelStyle = labelStyle,
                modifier = Modifier
                    .align(AbsoluteAlignment.CenterRight)
                    .absolutePadding(right = 12.dp),
            )
        }
    }
}

// アルファスライダー用数値バッジ（Previewのカラーコードラベルと統一された表現）
@Composable
private fun AlphaBadge(
    value: Int,
    scale: Float,
    alpha: Float,
    backgroundColor: Color,
    contentColor: Color,
    labelStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .heightIn(min = 22.dp)
            .widthIn(min = 36.dp)
            .background(backgroundColor, ChooserShapes.label)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.labelSmall.merge(labelStyle),
            color = contentColor,
            softWrap = false,
            textAlign = TextAlign.Center,
        )
    }
}

@PreviewEnvironment
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
                AlphaSlider(
                    value = 128,
                    onValueChange = {},
                    color = Color.Blue,
                    accessibilityLabel = "Alpha",
                )
                AlphaSlider(
                    value = 220,
                    onValueChange = {},
                    color = Color.Red,
                    accessibilityLabel = "Alpha Evading",
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
