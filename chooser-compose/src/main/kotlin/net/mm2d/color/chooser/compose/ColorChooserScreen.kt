/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.mm2d.color.chooser.compose.util.ColorSaver
import net.mm2d.color.chooser.compose.util.alphaBackgroundBrush
import net.mm2d.color.chooser.compose.util.to8bitInt

private val PreviewShape = RoundedCornerShape(16.dp)

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

object ColorChooserDefaults {
    val ContentSpacing: Dp = 12.dp
    val PreviewHeight: Dp = 64.dp

    val previewLabelStyle: TextStyle = TextStyle.Default.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        letterSpacing = 0.5.sp,
    )

    val tabTextStyle: TextStyle = TextStyle.Default.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp,
    )

    val sliderLabelStyle: TextStyle
        @Composable
        get() = MaterialTheme.typography.labelMedium.copy(
            fontSize = 12.sp,
        )

    @Composable
    fun colors(
        selectedTabContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        unselectedTabContentColor: Color = MaterialTheme.colorScheme.onSurface,
        selectedTabContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        unselectedTabContainerColor: Color = Color.Transparent,
        sliderLabelColor: Color = MaterialTheme.colorScheme.onSurface,
        previewLabelColor: Color = Color.White,
        previewLabelBackgroundColor: Color = Color.Black.copy(alpha = 0.5f),
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

/**
 * Color chooser screen.
 *
 * The edited color and selected tab are saved across activity and process recreation.
 * Callers that retain the result for a confirmation action should save that result as well.
 * Restoring the screen does not invoke [onColorChanged].
 *
 * @param initialColor initial color.
 * @param onColorChanged callback invoked when the selected color changes.
 * @param modifier modifier for the container layout.
 * @param withAlpha whether to show alpha control slider.
 * @param choosers list of choosers to display as tabs. Default is all choosers in [Chooser].
 * @param initialChooser initial chooser tab to select. Default is [Chooser.M2].
 * @param colors color palette for UI elements.
 * @param contentSpacing vertical spacing between sections.
 * @param previewHeight height of the color preview area.
 * @param previewLabelStyle text style for the preview color code.
 * @param tabTextStyle text style for the chooser tabs.
 * @param sliderLabelStyle text style for the slider value labels.
 * @param disableInnerScroll whether to disable vertical scrolling inside palettes. Set to true when
 * placing this screen in a vertically scrolling parent without a fixed height. Horizontal palette
 * scrolling remains enabled, so the parent must provide a bounded width.
 */
@Composable
fun ColorChooserScreen(
    initialColor: Color,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier,
    withAlpha: Boolean = true,
    choosers: List<Chooser> = Chooser.entries,
    initialChooser: Chooser = Chooser.M2,
    colors: ColorChooserColors = ColorChooserDefaults.colors(),
    contentSpacing: Dp = ColorChooserDefaults.ContentSpacing,
    previewHeight: Dp = ColorChooserDefaults.PreviewHeight,
    previewLabelStyle: TextStyle = ColorChooserDefaults.previewLabelStyle,
    tabTextStyle: TextStyle = ColorChooserDefaults.tabTextStyle,
    sliderLabelStyle: TextStyle = ColorChooserDefaults.sliderLabelStyle,
    disableInnerScroll: Boolean = false,
) {
    val initialAlpha = remember(initialColor, withAlpha) {
        if (withAlpha) initialColor.alpha.to8bitInt() else 255
    }
    var currentOpaque by rememberSaveable(initialColor, stateSaver = ColorSaver) {
        mutableStateOf(initialColor.copy(alpha = 1f))
    }
    var currentAlpha by rememberSaveable(initialColor, withAlpha) { mutableIntStateOf(initialAlpha) }

    val currentColor = currentOpaque.copy(alpha = if (withAlpha) currentAlpha / 255f else 1f)

    val updateColor = { color: Color, alphaInt: Int ->
        val alphaFloat = if (withAlpha) alphaInt / 255f else 1f
        onColorChanged(color.copy(alpha = alphaFloat))
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(contentSpacing),
    ) {
        ColorPreview(
            initialColor = initialColor,
            resultColor = currentColor,
            withAlpha = withAlpha,
            labelColor = colors.previewLabelColor,
            labelBackgroundColor = colors.previewLabelBackgroundColor,
            labelTextStyle = previewLabelStyle,
            modifier = Modifier
                .fillMaxWidth()
                .height(previewHeight),
        )
        if (withAlpha) {
            AlphaChooser(
                currentColor = currentOpaque,
                currentAlpha = currentAlpha,
                onAlphaChanged = { newAlpha ->
                    currentAlpha = newAlpha
                    updateColor(currentOpaque, newAlpha)
                },
                labelColor = colors.sliderLabelColor,
                labelStyle = sliderLabelStyle,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )
        }
        val validatedChoosers = choosers.ifEmpty { listOf(Chooser.M2) }
        var currentChooser by rememberSaveable(validatedChoosers, initialChooser) {
            val validatedInitialChooser =
                validatedChoosers.firstOrNull { it == initialChooser } ?: validatedChoosers.first()
            mutableStateOf(validatedInitialChooser)
        }
        if (validatedChoosers.size > 1) {
            ChooserSwitch(
                currentChooser = currentChooser,
                onChooserChanged = { currentChooser = it },
                choosers = validatedChoosers,
                selectedContentColor = colors.selectedTabContentColor,
                unselectedContentColor = colors.unselectedTabContentColor,
                selectedContainerColor = colors.selectedTabContainerColor,
                unselectedContainerColor = colors.unselectedTabContainerColor,
                tabTextStyle = tabTextStyle,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )
        }
        val onOpaqueChanged = { newColor: Color ->
            currentOpaque = newColor
            updateColor(newColor, currentAlpha)
        }
        when (currentChooser) {
            Chooser.M2 ->
                M2Chooser(
                    currentColor = currentOpaque,
                    onColorChanged = onOpaqueChanged,
                    disableInnerScroll = disableInnerScroll,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                )

            Chooser.HSV ->
                HsvChooser(
                    currentColor = currentOpaque,
                    onColorChanged = onOpaqueChanged,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                )

            Chooser.RGB ->
                RgbChooser(
                    currentColor = currentOpaque,
                    onColorChanged = onOpaqueChanged,
                    sliderLabelColor = colors.sliderLabelColor,
                    sliderLabelStyle = sliderLabelStyle,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                )

            Chooser.M3 ->
                M3Chooser(
                    currentColor = currentOpaque,
                    onColorChanged = onOpaqueChanged,
                    disableInnerScroll = disableInnerScroll,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                )
        }
    }
}

@Composable
private fun ColorPreview(
    initialColor: Color,
    resultColor: Color,
    withAlpha: Boolean,
    labelColor: Color,
    labelBackgroundColor: Color,
    labelTextStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(PreviewShape),
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .background(alphaBackgroundBrush()),
            color = initialColor,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = initialColor.toHex(withAlpha),
                    modifier = Modifier
                        .padding(8.dp)
                        .background(labelBackgroundColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.BottomStart),
                    style = labelTextStyle,
                    color = labelColor,
                )
            }
        }
        Surface(
            modifier = Modifier
                .weight(2f)
                .background(alphaBackgroundBrush()),
            color = resultColor,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = resultColor.toHex(withAlpha),
                    modifier = Modifier
                        .padding(8.dp)
                        .background(labelBackgroundColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.BottomEnd),
                    style = labelTextStyle,
                    color = labelColor,
                )
            }
        }
    }
}

private fun Color.toHex(
    withAlpha: Boolean,
): String =
    if (withAlpha) {
        "#%08X".format(toArgb())
    } else {
        "#%06X".format(toArgb() and 0xFFFFFF)
    }

@PreviewLightDark
@Composable
private fun PreviewColorChooserScreen() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
    ) {
        Scaffold {
            ColorChooserScreen(
                initialColor = Color.Red,
                onColorChanged = {},
                modifier = Modifier
                    .padding(it)
                    .padding(16.dp),
            )
        }
    }
}
