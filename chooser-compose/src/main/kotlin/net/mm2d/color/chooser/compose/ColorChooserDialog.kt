/*
 * Copyright (c) 2024 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import net.mm2d.color.chooser.compose.util.ColorSaver
import net.mm2d.color.chooser.compose.util.toChooserColor

/**
 * Color chooser dialog.
 *
 * @param onDismissRequest callback when the dialog is dismissed.
 * @param onConfirm callback when the color is confirmed. The chosen color is passed as argument.
 * @param modifier modifier for the dialog surface.
 * @param initialColor initial color, converted to 8-bit sRGB. If [Color.Unspecified], [Color.Black] is used.
 * @param withAlpha whether to edit alpha. If false, previews and the chosen color are opaque,
 * including when confirmed without editing. Disabling alpha discards transparency.
 * @param choosers list of choosers to show. Default is [Chooser.entries].
 * @param initialChooser initial chooser tab to select. Default is [Chooser.M2].
 * @param onColorChanged callback invoked whenever the editing color changes in the dialog.
 * @param colors colors used for styling tabs and sliders. See [ColorChooserDefaults.colors].
 * @param shape shape of the dialog.
 * @param containerColor color of the dialog container.
 * @param tonalElevation tonal elevation.
 * @param properties dialog properties.
 * @param contentSpacing spacing between content blocks in the chooser.
 * @param previewHeight height of the preview area.
 * @param previewLabelStyle text style of preview labels.
 * @param tabTextStyle text style of tab labels.
 * @param sliderLabelStyle text style of slider labels.
 * @param confirmButton composable slot for the confirmation button, receiving the currently selected color.
 * Default is a [Button] labeled "OK" calling [onConfirm] and [onDismissRequest].
 * @param dismissButton optional composable slot for the dismiss button.
 * Default is a [TextButton] labeled "Cancel" calling [onDismissRequest].
 */
@Composable
fun ColorChooserDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Color) -> Unit,
    modifier: Modifier = Modifier,
    initialColor: Color = Color.Unspecified,
    withAlpha: Boolean = false,
    choosers: List<Chooser> = Chooser.entries,
    initialChooser: Chooser = Chooser.M2,
    onColorChanged: ((Color) -> Unit)? = null,
    colors: ColorChooserColors = ColorChooserDefaults.colors(),
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    contentSpacing: Dp = ColorChooserDefaults.contentSpacing,
    previewHeight: Dp = ColorChooserDefaults.previewHeight,
    previewLabelStyle: TextStyle = ColorChooserDefaults.previewLabelStyle,
    tabTextStyle: TextStyle = ColorChooserDefaults.tabTextStyle,
    sliderLabelStyle: TextStyle = ColorChooserDefaults.sliderLabelStyle,
    confirmButton: @Composable (selectedColor: Color) -> Unit = { selectedColor ->
        Button(
            onClick = {
                onConfirm(selectedColor)
                onDismissRequest()
            },
        ) {
            Text(
                text = stringResource(id = R.string.mm2d_cc_ok),
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
    },
    dismissButton: @Composable (() -> Unit)? = {
        TextButton(
            onClick = onDismissRequest,
        ) {
            Text(
                text = stringResource(id = R.string.mm2d_cc_cancel),
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
    },
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Surface(
            modifier = modifier.size(calculateDialogSize()),
            shape = shape,
            color = containerColor,
            tonalElevation = tonalElevation,
        ) {
            Column {
                val normalizedInitialColor = remember(initialColor, withAlpha) {
                    if (initialColor.isSpecified) {
                        initialColor.toChooserColor(withAlpha)
                    } else {
                        Color.Black
                    }
                }
                var selectedColor by rememberSaveable(initialColor, stateSaver = ColorSaver) {
                    mutableStateOf(normalizedInitialColor)
                }
                if (!withAlpha && selectedColor.alpha != 1f) selectedColor = selectedColor.copy(alpha = 1f)
                ColorChooserContent(
                    initialColor = normalizedInitialColor,
                    currentColor = selectedColor,
                    onColorChanged = { color ->
                        selectedColor = color
                        onColorChanged?.invoke(color)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    withAlpha = withAlpha,
                    choosers = choosers,
                    initialChooser = initialChooser,
                    colors = colors,
                    contentSpacing = contentSpacing,
                    previewHeight = previewHeight,
                    previewLabelStyle = previewLabelStyle,
                    tabTextStyle = tabTextStyle,
                    sliderLabelStyle = sliderLabelStyle,
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    dismissButton?.let {
                        it()
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    confirmButton(selectedColor)
                }
            }
        }
    }
}

/**
 * Color chooser dialog.
 *
 * @param initialColor initial color, converted to 8-bit sRGB. Must not be [Color.Unspecified].
 * @param onDismissRequest callback when the dialog is dismissed.
 * @param onChooseColor callback when the color is chosen. chosen color is passed as argument.
 * @param modifier modifier.
 * @param withAlpha whether to edit alpha. If false, previews and the chosen color are opaque,
 * including when confirmed without editing. Disabling alpha discards transparency.
 * @param initialTab initial tab. default is [Tab.PALETTE]. see [Tab].
 * @param tabs tabs to show. default is [Tab.PALETTE], [Tab.HSV], [Tab.RGB]. see [Tab].
 * @param shape shape of the dialog.
 * @param containerColor color of the dialog container.
 * @param titleContentColor color of the chooser tab labels, both selected and unselected.
 * @param buttonContentColor color of the button content.
 * @param tonalElevation tonal elevation.
 * @param properties dialog properties.
 */
@Deprecated(
    message = "Use ColorChooserDialog with onDismissRequest and onConfirm instead.",
    replaceWith = ReplaceWith(
        "ColorChooserDialog(onDismissRequest = onDismissRequest, onConfirm = onChooseColor, initialColor = initialColor, withAlpha = withAlpha)",
    ),
)
@Suppress("DEPRECATION")
@Composable
fun ColorChooserDialog(
    initialColor: Color,
    onDismissRequest: () -> Unit,
    onChooseColor: (Color) -> Unit,
    modifier: Modifier = Modifier,
    withAlpha: Boolean = false,
    initialTab: Tab = Tab.DEFAULT_TAB,
    tabs: List<Tab> = Tab.DEFAULT_TABS,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    buttonContentColor: Color = MaterialTheme.colorScheme.primary,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Surface(
            modifier = modifier.size(calculateDialogSize()),
            shape = shape,
            color = containerColor,
            tonalElevation = tonalElevation,
        ) {
            Column {
                var selectedColor by rememberSaveable(initialColor, stateSaver = ColorSaver) {
                    mutableStateOf(initialColor.toChooserColor(withAlpha))
                }
                if (!withAlpha && selectedColor.alpha != 1f) selectedColor = selectedColor.copy(alpha = 1f)
                ColorChooserContent(
                    initialColor = initialColor.toChooserColor(withAlpha),
                    currentColor = selectedColor,
                    onColorChanged = { selectedColor = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    withAlpha = withAlpha,
                    choosers = tabs.map { it.toChooser() },
                    initialChooser = initialTab.toChooser(),
                    colors = ColorChooserDefaults.colors(
                        selectedTabContentColor = titleContentColor,
                        unselectedTabContentColor = titleContentColor,
                    ),
                )
                DialogButtons(
                    onDismissRequest = onDismissRequest,
                    onChooseColor = { onChooseColor(selectedColor) },
                    buttonContentColor = buttonContentColor,
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.DialogButtons(
    onDismissRequest: () -> Unit,
    onChooseColor: () -> Unit,
    buttonContentColor: Color,
) {
    Row(
        modifier = Modifier
            .align(Alignment.End)
            .padding(vertical = 8.dp),
    ) {
        TextButton(
            onClick = onDismissRequest,
            modifier = Modifier.padding(end = 8.dp),
        ) {
            Text(
                text = stringResource(id = R.string.mm2d_cc_cancel),
                color = buttonContentColor,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        TextButton(
            modifier = Modifier.padding(end = 16.dp),
            onClick = {
                onChooseColor()
                onDismissRequest()
            },
        ) {
            Text(
                text = stringResource(id = R.string.mm2d_cc_ok),
                color = buttonContentColor,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun calculateDialogSize(): DpSize {
    val density = LocalDensity.current
    val screenWidth = with(density) { LocalWindowInfo.current.containerSize.width.toDp() }
    val screenHeight = with(density) { LocalWindowInfo.current.containerSize.height.toDp() }

    val width = minOf((screenWidth * 0.9f), 480.dp)
    val height = if (screenHeight < 526.dp) {
        screenHeight * 0.95f
    } else if (screenHeight < 714.dp) {
        500.dp
    } else {
        screenHeight * 0.7f
    }
    return DpSize(width, height)
}

@Suppress("DEPRECATION")
internal fun Tab.toChooser(): Chooser =
    when (this) {
        Tab.PALETTE -> Chooser.M2
        Tab.HSV -> Chooser.HSV
        Tab.RGB -> Chooser.RGB
        Tab.M3 -> Chooser.M3
    }
