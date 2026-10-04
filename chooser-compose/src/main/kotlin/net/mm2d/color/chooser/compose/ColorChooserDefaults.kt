/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DefaultTextStyle =
    TextStyle.Default.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        ),
    )

object ColorChooserDefaults {
    val contentSpacing: Dp = 12.dp
    val previewHeight: Dp = 64.dp

    val previewLabelStyle: TextStyle = DefaultTextStyle.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.2.sp,
    )

    val tabTextStyle: TextStyle = DefaultTextStyle.copy(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    )

    val sliderLabelStyle: TextStyle = DefaultTextStyle.copy(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )

    @Composable
    fun colors(
        selectedTabContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        unselectedTabContentColor: Color = MaterialTheme.colorScheme.onSurface,
        selectedTabContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        unselectedTabContainerColor: Color = Color.Transparent,
        sliderLabelColor: Color = MaterialTheme.colorScheme.onSurface,
        previewLabelColor: Color = Color.White,
        previewLabelBackgroundColor: Color = Color.Black.copy(alpha = 0.75f),
    ): ColorChooserColors =
        ColorChooserColors(
            selectedTabContentColor = selectedTabContentColor,
            unselectedTabContentColor = unselectedTabContentColor,
            selectedTabContainerColor = selectedTabContainerColor,
            unselectedTabContainerColor = unselectedTabContainerColor,
            sliderLabelColor = sliderLabelColor,
            previewLabelColor = previewLabelColor,
            previewLabelBackgroundColor = previewLabelBackgroundColor,
        )
}
