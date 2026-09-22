/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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
