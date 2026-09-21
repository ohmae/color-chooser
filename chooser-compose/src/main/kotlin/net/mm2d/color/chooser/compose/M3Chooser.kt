/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
internal fun M3Chooser(
    currentColor: Color,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier,
    disableInnerScroll: Boolean = false,
) {
    val context = LocalContext.current
    val palette = remember { paletteMaterial3(context) }
    PaletteChooser(
        palette = palette,
        currentColor = currentColor,
        onColorChanged = onColorChanged,
        modifier = modifier,
        disableInnerScroll = disableInnerScroll,
    )
}
