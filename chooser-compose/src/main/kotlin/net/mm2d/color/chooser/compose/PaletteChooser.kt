/*
 * Copyright (c) 2024 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.util.shouldUseWhiteForeground

@Composable
internal fun PaletteChooser(
    colorEventState: MutableState<ColorEvent>,
    modifier: Modifier = Modifier,
) {
    var colorEvent by colorEventState
    val opacityColor = colorEvent.color.copy(alpha = 1f)
    LazyColumn(
        modifier = modifier,
    ) {
        items(palette) { colors ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(color)
                            .clickable {
                                colorEvent = ColorEvent(
                                    color.copy(alpha = colorEvent.color.alpha),
                                    ColorSource.PALETTE,
                                )
                            },
                    ) {
                        if (opacityColor == color) {
                            val tint =
                                if (color.shouldUseWhiteForeground()) {
                                    Color.White
                                } else {
                                    Color.Black
                                }
                            Image(
                                painter = painterResource(id = R.drawable.mm2d_cc_ic_check),
                                colorFilter = ColorFilter.tint(tint),
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }
                }
                repeat(14 - colors.size) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
            }
        }
    }
}
