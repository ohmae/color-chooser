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

@Composable
internal fun AlphaChooser(
    currentColor: Color,
    currentAlpha: Int,
    onAlphaChanged: (Int) -> Unit,
    labelColor: Color,
    labelStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    ColorSlider(
        value = currentAlpha,
        onValueChange = onAlphaChanged,
        color = currentColor,
        accessibilityLabel = stringResource(R.string.mm2d_cc_alpha),
        alphaMode = true,
        labelColor = labelColor,
        labelStyle = labelStyle,
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun PreviewAlphaChooser() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
    ) {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        ) {
            AlphaChooser(
                currentColor = Color.Red,
                currentAlpha = 128,
                onAlphaChanged = {},
                labelColor = MaterialTheme.colorScheme.onSurface,
                labelStyle = ColorChooserDefaults.sliderLabelStyle,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
