/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.util.shouldUseWhiteForeground

@Composable
internal fun PaletteChooser(
    palette: List<List<Color>>,
    currentColor: Color,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier,
    disableInnerScroll: Boolean = false,
) {
    val scrollModifier = if (disableInnerScroll) {
        Modifier
    } else {
        val scrollState = rememberScrollState()
        Modifier.verticalScroll(scrollState)
    }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .then(scrollModifier),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(palette) { list ->
            Column(
                modifier = Modifier.width(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                list.forEach { color ->
                    val selected = color == currentColor
                    val shape = RoundedCornerShape(if (selected) 16.dp else 4.dp)
                    Surface(
                        onClick = { onColorChanged(color) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .semantics {
                                contentDescription = "#%08X".format(color.toArgb())
                                this.selected = selected
                                role = Role.RadioButton
                            },
                        shape = shape,
                        color = color,
                    ) {
                        if (selected) {
                            val tint =
                                if (color.shouldUseWhiteForeground()) {
                                    Color.White
                                } else {
                                    Color.Black
                                }
                            Box(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.mm2d_cc_ic_check),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(tint),
                                    modifier = Modifier.align(Alignment.Center),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
