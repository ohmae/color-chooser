/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.util.TRACK_HEIGHT

internal val HSV_CONTENT_SPACING: Dp = 8.dp
private const val HSV_AREA_HEIGHT_FRACTION = 0.5f

internal data class ChooserLayout(
    val useEntireScroll: Boolean,
    val hsvAreaSize: Dp?,
    val disablePaletteScroll: Boolean,
)

internal fun calculateChooserLayout(
    currentChooser: Chooser,
    hasBoundedHeight: Boolean,
    scrollEntireContent: Boolean,
    headerHeight: Dp,
    contentSpacing: Dp,
    maxWidth: Dp,
    maxHeight: Dp,
    disableInnerScroll: Boolean,
): ChooserLayout {
    val isPalette = currentChooser == Chooser.M2 || currentChooser == Chooser.M3
    // RGB も HSV と同じ高さを基準に、自動スクロールの要否を判定する従来の仕様を維持する。
    val hsvOverflows = headerHeight + contentSpacing + TRACK_HEIGHT + HSV_CONTENT_SPACING + maxWidth > maxHeight
    val useEntireScroll = hasBoundedHeight && (scrollEntireContent || (!isPalette && hsvOverflows))
    // SV 面の外にスクロールを開始できる余地を残す。全体スクロール指定時も同じ縮小率を使う。
    val hsvAreaSize = if (hasBoundedHeight && (scrollEntireContent || hsvOverflows)) {
        maxHeight * HSV_AREA_HEIGHT_FRACTION
    } else {
        null
    }
    return ChooserLayout(
        useEntireScroll = useEntireScroll,
        hsvAreaSize = hsvAreaSize,
        disablePaletteScroll = disableInnerScroll || useEntireScroll,
    )
}
