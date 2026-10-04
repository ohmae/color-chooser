/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.mm2d.color.chooser.compose.util.ColorSaver
import net.mm2d.color.chooser.compose.util.TRACK_HEIGHT
import net.mm2d.color.chooser.compose.util.to8bitInt
import net.mm2d.color.chooser.compose.util.toChooserColor

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

/**
 * Color chooser screen.
 *
 * The edited color, selected tab and HSV editing coordinates are saved across activity and process recreation.
 * Callers that retain the result for a confirmation action should save that result as well.
 * Restoring the screen does not invoke [onColorChanged].
 * Inputs are converted to 8-bit sRGB for previews and editing; callbacks return 8-bit sRGB colors.
 * [Color.Unspecified] is not supported. Disabling alpha discards transparency, including when
 * toggled during editing. Enabling it again starts with full opacity.
 * In a bounded height, overflowing RGB and HSV controls scroll together with the preview;
 * palettes keep their own vertical scrolling. The overflowing HSV panel is reduced to leave
 * an area for starting vertical scroll gestures outside its two-axis editing surface.
 *
 * @param initialColor initial color.
 * @param onColorChanged callback invoked when the selected color changes.
 * @param modifier modifier for the container layout.
 * @param withAlpha whether to edit alpha. If false, the preview and result are always opaque.
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
    contentSpacing: Dp = ColorChooserDefaults.contentSpacing,
    previewHeight: Dp = ColorChooserDefaults.previewHeight,
    previewLabelStyle: TextStyle = ColorChooserDefaults.previewLabelStyle,
    tabTextStyle: TextStyle = ColorChooserDefaults.tabTextStyle,
    sliderLabelStyle: TextStyle = ColorChooserDefaults.sliderLabelStyle,
    disableInnerScroll: Boolean = false,
) {
    val normalizedInitialColor = remember(initialColor, withAlpha) {
        initialColor.toChooserColor(withAlpha)
    }
    var currentColor by rememberSaveable(initialColor, stateSaver = ColorSaver) {
        mutableStateOf(normalizedInitialColor)
    }
    if (!withAlpha && currentColor.alpha != 1f) currentColor = currentColor.copy(alpha = 1f)

    ColorChooserScreen(
        color = currentColor,
        onColorChanged = {
            currentColor = it
            onColorChanged(it)
        },
        modifier = modifier,
        initialColor = normalizedInitialColor,
        withAlpha = withAlpha,
        choosers = choosers,
        initialChooser = initialChooser,
        colors = colors,
        contentSpacing = contentSpacing,
        previewHeight = previewHeight,
        previewLabelStyle = previewLabelStyle,
        tabTextStyle = tabTextStyle,
        sliderLabelStyle = sliderLabelStyle,
        disableInnerScroll = disableInnerScroll,
    )
}

/**
 * Stateless color chooser screen.
 *
 * External updates to [color] are reflected immediately.
 * [color] is converted to 8-bit sRGB; [Color.Unspecified] is not supported.
 * Disabling alpha discards transparency, and callbacks return 8-bit sRGB colors.
 * In a bounded height, overflowing RGB and HSV controls scroll together with the preview;
 * palettes keep their own vertical scrolling.
 *
 * @param color current color to display and edit.
 * @param onColorChanged callback invoked when the selected color changes.
 * @param modifier modifier for the container layout.
 * @param initialColor initial color displayed in the preview for comparison. If null, [color] is used.
 * @param withAlpha whether to edit alpha. If false, the preview and result are always opaque.
 * @param choosers list of choosers to display as tabs. Default is all choosers in [Chooser].
 * @param initialChooser initial chooser tab to select. Default is [Chooser.M2].
 * @param colors color palette for UI elements.
 * @param contentSpacing vertical spacing between sections.
 * @param previewHeight height of the color preview area.
 * @param previewLabelStyle text style for the preview color code.
 * @param tabTextStyle text style for the chooser tabs.
 * @param sliderLabelStyle text style for the slider value labels.
 * @param disableInnerScroll whether to disable vertical scrolling inside palettes.
 */
@Composable
fun ColorChooserScreen(
    color: Color,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier,
    initialColor: Color? = null,
    withAlpha: Boolean = true,
    choosers: List<Chooser> = Chooser.entries,
    initialChooser: Chooser = Chooser.M2,
    colors: ColorChooserColors = ColorChooserDefaults.colors(),
    contentSpacing: Dp = ColorChooserDefaults.contentSpacing,
    previewHeight: Dp = ColorChooserDefaults.previewHeight,
    previewLabelStyle: TextStyle = ColorChooserDefaults.previewLabelStyle,
    tabTextStyle: TextStyle = ColorChooserDefaults.tabTextStyle,
    sliderLabelStyle: TextStyle = ColorChooserDefaults.sliderLabelStyle,
    disableInnerScroll: Boolean = false,
) {
    val normalizedCurrentColor = color.toChooserColor(withAlpha)
    val effectiveInitialColor = (initialColor ?: color).toChooserColor(withAlpha)

    ColorChooserContent(
        initialColor = effectiveInitialColor,
        currentColor = normalizedCurrentColor,
        onColorChanged = onColorChanged,
        modifier = modifier,
        withAlpha = withAlpha,
        choosers = choosers,
        initialChooser = initialChooser,
        colors = colors,
        contentSpacing = contentSpacing,
        previewHeight = previewHeight,
        previewLabelStyle = previewLabelStyle,
        tabTextStyle = tabTextStyle,
        sliderLabelStyle = sliderLabelStyle,
        disableInnerScroll = disableInnerScroll,
    )
}

// 選択した色を保持する画面と従来の API で共用する UI。
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun ColorChooserContent(
    initialColor: Color,
    currentColor: Color,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier,
    withAlpha: Boolean = true,
    choosers: List<Chooser> = Chooser.entries,
    initialChooser: Chooser = Chooser.M2,
    colors: ColorChooserColors = ColorChooserDefaults.colors(),
    contentSpacing: Dp = ColorChooserDefaults.contentSpacing,
    previewHeight: Dp = ColorChooserDefaults.previewHeight,
    previewLabelStyle: TextStyle = ColorChooserDefaults.previewLabelStyle,
    tabTextStyle: TextStyle = ColorChooserDefaults.tabTextStyle,
    sliderLabelStyle: TextStyle = ColorChooserDefaults.sliderLabelStyle,
    disableInnerScroll: Boolean = false,
    maxHsvAreaSize: Dp? = null,
    scrollEntireContent: Boolean = false,
) {
    val currentOpaque = currentColor.copy(alpha = 1f)
    val hsvState = rememberHsvChooserState(currentOpaque)
    val currentAlpha = currentColor.alpha.to8bitInt()
    val updateColor = { color: Color, alphaInt: Int ->
        val alphaFloat = if (withAlpha) alphaInt / 255f else 1f
        onColorChanged(color.copy(alpha = alphaFloat))
    }

    val validatedChoosers = choosers.ifEmpty { listOf(Chooser.M2) }
    var currentChooser by rememberSaveable(validatedChoosers, initialChooser) {
        val validatedInitialChooser =
            validatedChoosers.firstOrNull { it == initialChooser } ?: validatedChoosers.first()
        mutableStateOf(validatedInitialChooser)
    }
    val scrollState = rememberScrollState()
    var headerHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    BoxWithConstraints(modifier = modifier) {
        val layout = calculateChooserLayout(
            currentChooser = currentChooser,
            hasBoundedHeight = constraints.hasBoundedHeight,
            scrollEntireContent = scrollEntireContent,
            headerHeight = with(density) { headerHeightPx.toDp() },
            contentSpacing = contentSpacing,
            maxWidth = maxWidth,
            maxHeight = maxHeight,
            maxHsvAreaSize = maxHsvAreaSize,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (layout.useEntireScroll) {
                        Modifier.verticalScroll(scrollState, enabled = scrollState.maxValue > 0)
                    } else {
                        Modifier
                    },
                )
                .padding(bottom = if (layout.useEntireScroll) 32.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(contentSpacing),
        ) {
            ColorChooserHeader(
                initialColor = initialColor,
                currentColor = currentColor,
                currentOpaque = currentOpaque,
                currentAlpha = currentAlpha,
                onAlphaChanged = { updateColor(currentOpaque, it) },
                withAlpha = withAlpha,
                currentChooser = currentChooser,
                onChooserChanged = { currentChooser = it },
                choosers = validatedChoosers,
                colors = colors,
                contentSpacing = contentSpacing,
                previewHeight = previewHeight,
                previewLabelStyle = previewLabelStyle,
                tabTextStyle = tabTextStyle,
                modifier = Modifier.onSizeChanged { headerHeightPx = it.height },
            )
            AnimatedChooserPanel(
                currentChooser = currentChooser,
                choosers = validatedChoosers,
                currentColor = currentOpaque,
                onColorChanged = { updateColor(it, currentAlpha) },
                hsvState = hsvState,
                hsvAreaSize = layout.hsvAreaSize,
                disableInnerScroll = disableInnerScroll || layout.useEntireScroll,
                sliderLabelColor = colors.sliderLabelColor,
                sliderLabelStyle = sliderLabelStyle,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

private data class ChooserLayout(
    val useEntireScroll: Boolean,
    val hsvAreaSize: Dp?,
)

private fun calculateChooserLayout(
    currentChooser: Chooser,
    hasBoundedHeight: Boolean,
    scrollEntireContent: Boolean,
    headerHeight: Dp,
    contentSpacing: Dp,
    maxWidth: Dp,
    maxHeight: Dp,
    maxHsvAreaSize: Dp?,
): ChooserLayout {
    val isPalette = currentChooser == Chooser.M2 || currentChooser == Chooser.M3
    // SV 面の外にスクロールを開始できる余地を残す。収まる場合は従来の正方形を維持する。
    val hsvOverflows = headerHeight + contentSpacing + TRACK_HEIGHT + 8.dp + maxWidth > maxHeight
    val useEntireScroll = hasBoundedHeight && (scrollEntireContent || !isPalette && hsvOverflows)
    val hsvAreaSize = maxHsvAreaSize ?: if (hasBoundedHeight && hsvOverflows) maxHeight * 0.5f else null
    return ChooserLayout(useEntireScroll, hsvAreaSize)
}

@Composable
private fun ColorChooserHeader(
    initialColor: Color,
    currentColor: Color,
    currentOpaque: Color,
    currentAlpha: Int,
    onAlphaChanged: (Int) -> Unit,
    withAlpha: Boolean,
    currentChooser: Chooser,
    onChooserChanged: (Chooser) -> Unit,
    choosers: List<Chooser>,
    colors: ColorChooserColors,
    contentSpacing: Dp,
    previewHeight: Dp,
    previewLabelStyle: TextStyle,
    tabTextStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
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
                onAlphaChanged = onAlphaChanged,
                labelColor = colors.previewLabelColor,
                labelBackgroundColor = colors.previewLabelBackgroundColor,
                labelStyle = previewLabelStyle,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )
        }
        if (choosers.size > 1) {
            ChooserSwitch(
                currentChooser = currentChooser,
                onChooserChanged = onChooserChanged,
                choosers = choosers,
                selectedContentColor = colors.selectedTabContentColor,
                unselectedContentColor = colors.unselectedTabContentColor,
                selectedContainerColor = colors.selectedTabContainerColor,
                unselectedContainerColor = colors.unselectedTabContainerColor,
                tabTextStyle = tabTextStyle,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
private fun AnimatedChooserPanel(
    currentChooser: Chooser,
    choosers: List<Chooser>,
    currentColor: Color,
    onColorChanged: (Color) -> Unit,
    hsvState: HsvChooserState,
    hsvAreaSize: Dp?,
    disableInnerScroll: Boolean,
    sliderLabelColor: Color,
    sliderLabelStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = currentChooser,
        transitionSpec = {
            chooserTabTransition(choosers)
        },
        contentAlignment = Alignment.TopCenter,
        label = "chooserTabTransition",
        modifier = modifier
            .fillMaxWidth(),
    ) { targetChooser ->
        when (targetChooser) {
            Chooser.M2 ->
                M2Chooser(
                    currentColor = currentColor,
                    onColorChanged = onColorChanged,
                    disableInnerScroll = disableInnerScroll,
                )

            Chooser.HSV ->
                HsvChooser(
                    currentColor = currentColor,
                    state = hsvState,
                    onColorChanged = onColorChanged,
                    // タブ切り替え中の高さ制約で SV 面が縮まないよう、固有の高さで測定する。
                    modifier = Modifier.wrapContentHeight(align = Alignment.Top, unbounded = true),
                    maxSaturationValueSize = hsvAreaSize,
                )

            Chooser.RGB ->
                RgbChooser(
                    currentColor = currentColor,
                    onColorChanged = onColorChanged,
                    sliderLabelColor = sliderLabelColor,
                    sliderLabelStyle = sliderLabelStyle,
                )

            Chooser.M3 ->
                M3Chooser(
                    currentColor = currentColor,
                    onColorChanged = onColorChanged,
                    disableInnerScroll = disableInnerScroll,
                )
        }
    }
}

private fun AnimatedContentTransitionScope<Chooser>.chooserTabTransition(
    choosers: List<Chooser>,
): ContentTransform {
    val initialIndex = choosers.indexOf(initialState)
    val targetIndex = choosers.indexOf(targetState)
    val slideFraction = 0.2f * if (targetIndex >= initialIndex) 1f else -1f
    val slideSpec = spring<IntOffset>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )
    val fadeSpec = spring<Float>(stiffness = Spring.StiffnessMediumLow)
    val enter = slideInHorizontally(
        animationSpec = slideSpec,
        initialOffsetX = { width -> (width * slideFraction).toInt() },
    ) + fadeIn(animationSpec = fadeSpec)
    val exit = slideOutHorizontally(
        animationSpec = slideSpec,
        targetOffsetX = { width -> -(width * slideFraction).toInt() },
    ) + fadeOut(animationSpec = fadeSpec)
    return (enter togetherWith exit).using(null)
}

private data class ScreenPreviewParameter(
    val chooser: Chooser,
    val withAlpha: Boolean,
    val initialColor: Color,
    val color: Color,
)

private class ChooserPreviewParameterProvider : PreviewParameterProvider<ScreenPreviewParameter> {
    override val values: Sequence<ScreenPreviewParameter> = sequenceOf(
        ScreenPreviewParameter(
            chooser = Chooser.M2,
            withAlpha = false,
            initialColor = Color(0xFFE53935),
            color = Color(0xFFF44336),
        ),
        ScreenPreviewParameter(
            chooser = Chooser.HSV,
            withAlpha = true,
            initialColor = Color(0x80FF0000),
            color = Color(0xCC00BCD4),
        ),
        ScreenPreviewParameter(
            chooser = Chooser.RGB,
            withAlpha = false,
            initialColor = Color.Red,
            color = Color.Magenta,
        ),
        ScreenPreviewParameter(
            chooser = Chooser.M3,
            withAlpha = true,
            initialColor = Color(0x804F378B),
            color = Color(0xCC6750A4),
        ),
    )
}

@PreviewEnvironment
@Composable
private fun PreviewColorChooserScreen(
    @PreviewParameter(ChooserPreviewParameterProvider::class) parameter: ScreenPreviewParameter,
) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
    ) {
        Scaffold {
            ColorChooserScreen(
                color = parameter.color,
                initialColor = parameter.initialColor,
                onColorChanged = {},
                withAlpha = parameter.withAlpha,
                initialChooser = parameter.chooser,
                modifier = Modifier
                    .padding(it)
                    .padding(16.dp),
            )
        }
    }
}
