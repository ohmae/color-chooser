/*
 * Copyright (c) 2024 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.R

private val colorBorder1 = Color.White
private val colorBorder2 = Color(0x1a000000)

internal fun Modifier.frameDecoration(): Modifier =
    this
        .background(colorBorder2)
        .padding(1.dp)
        .background(colorBorder1)
        .padding(2.dp)

@Composable
internal fun alphaBackgroundBrush(): ShaderBrush =
    ShaderBrush(
        ImageShader(
            ImageBitmap.imageResource(id = R.drawable.mm2d_cc_bg_alpha),
            TileMode.Repeated,
            TileMode.Repeated,
        ),
    )

@Composable
internal fun ColorControlGrip(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(16.dp, 16.dp)
            .clip(CircleShape)
            .background(colorBorder2)
            .padding(1.dp)
            .clip(CircleShape)
            .background(colorBorder1)
            .padding(2.dp)
            .clip(CircleShape)
            .background(color),
    )
}
