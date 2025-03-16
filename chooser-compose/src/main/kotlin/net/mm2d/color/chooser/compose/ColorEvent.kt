package net.mm2d.color.chooser.compose

import androidx.compose.ui.graphics.Color

internal data class ColorEvent(
    val color: Color,
    val source: ColorSource,
)
