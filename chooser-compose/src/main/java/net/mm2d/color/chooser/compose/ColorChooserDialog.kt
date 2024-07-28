package net.mm2d.color.chooser.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
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
    buttonContentColor: Color = AlertDialogDefaults.textContentColor,
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
                val colorState = rememberSaveable(stateSaver = ColorSaver) {
                    mutableStateOf(if (withAlpha) initialColor else initialColor.copy(alpha = 1f))
                }
                ColorChooserView(
                    colorState = colorState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    withAlpha = withAlpha,
                    initialTab = initialTab,
                    tabs = tabs,
                    titleContentColor = titleContentColor,
                )
                DialogButtons(
                    onDismissRequest = onDismissRequest,
                    onChooseColor = { onChooseColor(colorState.value) },
                    textContentColor = buttonContentColor,
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.DialogButtons(
    onDismissRequest: () -> Unit,
    onChooseColor: () -> Unit,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
) {
    Row(
        modifier = Modifier.align(Alignment.End),
    ) {
        TextButton(
            modifier = Modifier.padding(vertical = 8.dp),
            onClick = onDismissRequest,
        ) {
            Text(
                text = stringResource(id = R.string.mm2d_cc_cancel),
                color = textContentColor,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        TextButton(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 16.dp),
            onClick = {
                onChooseColor()
                onDismissRequest()
            },
        ) {
            Text(
                text = stringResource(id = R.string.mm2d_cc_ok),
                color = textContentColor,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun calculateDialogSize(): DpSize {
    val screenWith = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val portraitHeight = (screenHeight * 0.7f).dp

    val width = minOf((screenWith * 0.9f).dp, 480.dp)
    val height = if (portraitHeight < 500.dp) {
        (screenHeight * 0.95f).dp
    } else {
        minOf(portraitHeight, 640.dp)
    }
    return DpSize(width, height)
}
