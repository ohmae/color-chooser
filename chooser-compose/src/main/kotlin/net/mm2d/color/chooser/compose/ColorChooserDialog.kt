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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import net.mm2d.color.chooser.compose.util.ColorSaver

/**
 * Color chooser dialog.
 *
 * @param initialColor initial color.
 * @param onDismissRequest callback when the dialog is dismissed.
 * @param onChooseColor callback when the color is chosen. chosen color is passed as argument.
 * @param modifier modifier.
 * @param withAlpha whether to show alpha control.
 * @param initialTab initial tab. default is [Tab.PALETTE]. see [Tab].
 * @param tabs tabs to show. default is [Tab.PALETTE], [Tab.HSV], [Tab.RGB]. see [Tab].
 * @param shape shape of the dialog.
 * @param containerColor color of the dialog container.
 * @param titleContentColor color of the title content.
 * @param buttonContentColor color of the button content.
 * @param tonalElevation tonal elevation.
 * @param properties dialog properties.
 */
@Deprecated("use ColorChooserScreen instead.")
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
                var selectedColor by rememberSaveable(stateSaver = ColorSaver) {
                    mutableStateOf(initialColor)
                }
                ColorChooserScreen(
                    initialColor = initialColor,
                    onColorChanged = { selectedColor = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    withAlpha = withAlpha,
                    choosers = tabs.map { it.toChooser() },
                    initialChooser = initialTab.toChooser(),
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

internal fun Tab.toChooser(): Chooser =
    when (this) {
        Tab.PALETTE -> Chooser.M2
        Tab.HSV -> Chooser.HSV
        Tab.RGB -> Chooser.RGB
        Tab.M3 -> Chooser.M3
    }
