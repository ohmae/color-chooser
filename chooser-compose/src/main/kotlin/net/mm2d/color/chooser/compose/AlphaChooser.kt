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
import androidx.compose.ui.text.TextStyle

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
        alphaMode = true,
        labelColor = labelColor,
        labelStyle = labelStyle,
        modifier = modifier,
    )
}
