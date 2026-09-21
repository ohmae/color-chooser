/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

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
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        choosers.forEachIndexed { index, chooser ->
            ToggleButton(
                checked = chooser == currentChooser,
                onCheckedChange = { onChooserChanged(chooser) },
                modifier = Modifier.semantics { role = Role.RadioButton },
                buttonSize = ToggleButtonSize.Small,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                colors = ToggleButtonDefaults.colors(
                    checkedContentColor = selectedContentColor,
                    contentColor = unselectedContentColor,
                    checkedContainerColor = selectedContainerColor,
                    containerColor = unselectedContainerColor,
                ),
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    choosers.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
            ) {
                Text(
                    text = chooser.name,
                    style = tabTextStyle,
                )
            }
        }
    }
}
