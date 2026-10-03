package net.mm2d.color.chooser.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.util.ChooserShapes
import net.mm2d.color.chooser.compose.util.alphaBackgroundBrush

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun ColorPreview(
    initialColor: Color,
    resultColor: Color,
    withAlpha: Boolean,
    labelColor: Color,
    labelBackgroundColor: Color,
    labelTextStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    val intinalColorHel = remember(initialColor, withAlpha) { initialColor.toHex(withAlpha) }
    val resultColorHex = remember(resultColor, withAlpha) { resultColor.toHex(withAlpha) }
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
            .clip(ChooserShapes.preview),
    ) {
        val enlargedText = LocalDensity.current.fontScale > 1.3f
        val equalWidths = maxWidth < 360.dp || enlargedText
        val bottomPadding = if (enlargedText) 4.dp else 8.dp
        val labelVerticalPadding = if (enlargedText) 2.dp else 4.dp
        Row(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .background(alphaBackgroundBrush()),
                color = initialColor,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(
                        text = intinalColorHel,
                        modifier = Modifier
                            .semantics { contentDescription = intinalColorHel }
                            .padding(start = 8.dp, end = 4.dp, bottom = bottomPadding)
                            .background(labelBackgroundColor, ChooserShapes.label)
                            .padding(horizontal = 6.dp, vertical = labelVerticalPadding)
                            .align(Alignment.BottomStart),
                        style = MaterialTheme.typography.labelSmall.merge(labelTextStyle).copy(
                            fontWeight = FontWeight.Medium,
                        ),
                        color = labelColor,
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .weight(if (equalWidths) 1f else 2f)
                    .background(alphaBackgroundBrush()),
                color = resultColor,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(
                        text = resultColorHex,
                        modifier = Modifier
                            .semantics { contentDescription = resultColorHex }
                            .padding(start = 4.dp, end = 8.dp, bottom = bottomPadding)
                            .background(labelBackgroundColor, ChooserShapes.label)
                            .padding(horizontal = 6.dp, vertical = labelVerticalPadding)
                            .align(Alignment.BottomEnd),
                        style = MaterialTheme.typography.labelSmall.merge(labelTextStyle),
                        color = labelColor,
                    )
                }
            }
        }
    }
}

private fun Color.toHex(
    withAlpha: Boolean,
): String =
    if (withAlpha) {
        "#%08X".format(toArgb())
    } else {
        "#%06X".format(toArgb() and 0xFFFFFF)
    }

@PreviewLightDark
@Composable
private fun PreviewColorPreview() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
    ) {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        ) {
            val colors = ColorChooserDefaults.colors()
            ColorPreview(
                initialColor = Color.Red,
                resultColor = Color.Blue.copy(alpha = 0.5f),
                withAlpha = true,
                labelColor = colors.previewLabelColor,
                labelBackgroundColor = colors.previewLabelBackgroundColor,
                labelTextStyle = ColorChooserDefaults.previewLabelStyle,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(ColorChooserDefaults.previewHeight),
            )
        }
    }
}
