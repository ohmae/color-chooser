/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
class ColorChooserColors internal constructor(
    val selectedTabContentColor: Color,
    val unselectedTabContentColor: Color,
    val selectedTabContainerColor: Color,
    val unselectedTabContainerColor: Color,
    val sliderLabelColor: Color,
    val previewLabelColor: Color,
    val previewLabelBackgroundColor: Color,
) {
    fun copy(
        selectedTabContentColor: Color = this.selectedTabContentColor,
        unselectedTabContentColor: Color = this.unselectedTabContentColor,
        selectedTabContainerColor: Color = this.selectedTabContainerColor,
        unselectedTabContainerColor: Color = this.unselectedTabContainerColor,
        sliderLabelColor: Color = this.sliderLabelColor,
        previewLabelColor: Color = this.previewLabelColor,
        previewLabelBackgroundColor: Color = this.previewLabelBackgroundColor,
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

    override fun equals(
        other: Any?,
    ): Boolean {
        if (this === other) return true
        if (other !is ColorChooserColors) return false

        if (selectedTabContentColor != other.selectedTabContentColor) return false
        if (unselectedTabContentColor != other.unselectedTabContentColor) return false
        if (selectedTabContainerColor != other.selectedTabContainerColor) return false
        if (unselectedTabContainerColor != other.unselectedTabContainerColor) return false
        if (sliderLabelColor != other.sliderLabelColor) return false
        if (previewLabelColor != other.previewLabelColor) return false
        if (previewLabelBackgroundColor != other.previewLabelBackgroundColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = selectedTabContentColor.hashCode()
        result = 31 * result + unselectedTabContentColor.hashCode()
        result = 31 * result + selectedTabContainerColor.hashCode()
        result = 31 * result + unselectedTabContainerColor.hashCode()
        result = 31 * result + sliderLabelColor.hashCode()
        result = 31 * result + previewLabelColor.hashCode()
        result = 31 * result + previewLabelBackgroundColor.hashCode()
        return result
    }
}
