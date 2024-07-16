package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import net.mm2d.color.chooser.Tab
import net.mm2d.color.chooser.compose.util.ColorSaver

/**
 * Color chooser dialog.
 *
 * @param initialColor initial color.
 * @param onDismissRequest callback when the dialog is dismissed.
 * @param onChooseColor callback when the color is chosen. chosen color is passed as argument.
 * @param withAlpha whether to show alpha control.
 * @param initialTab initial tab. default is [Tab.PALETTE]. see [Tab].
 * @param tabs tabs to show. default is [Tab.PALETTE], [Tab.HSV], [Tab.RGB]. see [Tab].
 */
@Composable
fun ColorChooserDialog(
    initialColor: Color,
    onDismissRequest: () -> Unit,
    onChooseColor: (Color) -> Unit,
    withAlpha: Boolean = false,
    initialTab: Tab = Tab.DEFAULT_TAB,
    tabs: List<Tab> = Tab.DEFAULT_TABS,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .size(calculateDialogSize())
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface),
        ) {
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
            )
            Row(
                modifier = Modifier.align(Alignment.End),
            ) {
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text(text = stringResource(id = R.string.mm2d_cc_cancel))
                }
                TextButton(
                    onClick = {
                        onChooseColor(colorState.value)
                        onDismissRequest()
                    },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text(text = stringResource(id = R.string.mm2d_cc_ok))
                }
            }
        }
    }
}

@Composable
private fun calculateDialogSize(): DpSize {
    val screenWith = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val portraitHeight = (screenHeight * 0.65f).dp

    val width = minOf((screenWith * 0.9f).dp, 480.dp)
    val height = if (portraitHeight < 500.dp) {
        (screenHeight * 0.95f).dp
    } else {
        minOf(portraitHeight, 640.dp)
    }
    return DpSize(width, height)
}
