package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.util.alphaBackgroundBrush

private val PreviewShape = RoundedCornerShape(24.dp)
private val PreviewLabelShape = RoundedCornerShape(percent = 50)

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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(PreviewShape),
    ) {
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
                    text = initialColor.toHex(withAlpha),
                    modifier = Modifier
                        .padding(start = 16.dp, bottom = 8.dp)
                        .background(labelBackgroundColor, PreviewLabelShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .align(Alignment.BottomStart),
                    style = labelTextStyle,
                    color = labelColor,
                )
            }
        }
        Surface(
            modifier = Modifier
                .weight(2f)
                .background(alphaBackgroundBrush()),
            color = resultColor,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = resultColor.toHex(withAlpha),
                    modifier = Modifier
                        .padding(end = 16.dp, bottom = 8.dp)
                        .background(labelBackgroundColor, PreviewLabelShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .align(Alignment.BottomEnd),
                    style = labelTextStyle,
                    color = labelColor,
                )
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
