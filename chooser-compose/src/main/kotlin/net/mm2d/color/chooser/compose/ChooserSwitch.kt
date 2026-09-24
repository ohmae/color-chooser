/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChooserSwitch(
    currentChooser: Chooser,
    onChooserChanged: (Chooser) -> Unit,
    choosers: List<Chooser>,
    selectedContentColor: Color,
    unselectedContentColor: Color,
    selectedContainerColor: Color,
    unselectedContainerColor: Color,
    tabTextStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier,
    ) {
        choosers.forEachIndexed { index, chooser ->
            SegmentedButton(
                selected = chooser == currentChooser,
                onClick = { onChooserChanged(chooser) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = choosers.size,
                ),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = selectedContainerColor,
                    activeContentColor = selectedContentColor,
                    inactiveContainerColor = unselectedContainerColor,
                    inactiveContentColor = unselectedContentColor,
                ),
                label = {
                    Text(
                        text = chooser.name,
                        style = tabTextStyle,
                    )
                },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewChooserSwitch() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
    ) {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        ) {
            val colors = ColorChooserDefaults.colors()
            ChooserSwitch(
                currentChooser = Chooser.M2,
                onChooserChanged = {},
                choosers = Chooser.entries,
                selectedContentColor = colors.selectedTabContentColor,
                unselectedContentColor = colors.unselectedTabContentColor,
                selectedContainerColor = colors.selectedTabContainerColor,
                unselectedContainerColor = colors.unselectedTabContainerColor,
                tabTextStyle = ColorChooserDefaults.tabTextStyle,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
