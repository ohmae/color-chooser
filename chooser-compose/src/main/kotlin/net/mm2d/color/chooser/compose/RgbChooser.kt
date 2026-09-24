/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.util.to8bitInt

@Composable
internal fun RgbChooser(
    currentColor: Color,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier,
    sliderLabelColor: Color,
    sliderLabelStyle: TextStyle,
) {
    val red = currentColor.red.to8bitInt()
    val green = currentColor.green.to8bitInt()
    val blue = currentColor.blue.to8bitInt()

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        ColorSlider(
            value = red,
            onValueChange = { newRed ->
                onColorChanged(Color(newRed, green, blue))
            },
            color = Color.Red,
            accessibilityLabel = stringResource(R.string.mm2d_cc_red),
            labelColor = sliderLabelColor,
            labelStyle = sliderLabelStyle,
            modifier = Modifier.fillMaxWidth(),
        )
        ColorSlider(
            value = green,
            onValueChange = { newGreen ->
                onColorChanged(Color(red, newGreen, blue))
            },
            color = Color.Green,
            accessibilityLabel = stringResource(R.string.mm2d_cc_green),
            labelColor = sliderLabelColor,
            labelStyle = sliderLabelStyle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        )
        ColorSlider(
            value = blue,
            onValueChange = { newBlue ->
                onColorChanged(Color(red, green, newBlue))
            },
            color = Color.Blue,
            accessibilityLabel = stringResource(R.string.mm2d_cc_blue),
            labelColor = sliderLabelColor,
            labelStyle = sliderLabelStyle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewRgbChooser() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
    ) {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        ) {
            RgbChooser(
                currentColor = Color.Magenta,
                onColorChanged = {},
                sliderLabelColor = MaterialTheme.colorScheme.onSurface,
                sliderLabelStyle = ColorChooserDefaults.sliderLabelStyle,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
