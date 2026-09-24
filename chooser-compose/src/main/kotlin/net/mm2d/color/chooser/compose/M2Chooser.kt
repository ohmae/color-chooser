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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

@Composable
internal fun M2Chooser(
    currentColor: Color,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier,
    disableInnerScroll: Boolean = false,
) {
    PaletteChooser(
        palette = paletteMaterial2,
        currentColor = currentColor,
        onColorChanged = onColorChanged,
        modifier = modifier,
        disableInnerScroll = disableInnerScroll,
    )
}

@PreviewLightDark
@Composable
private fun PreviewM2Chooser() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
    ) {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        ) {
            M2Chooser(
                currentColor = Color.Red,
                onColorChanged = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
