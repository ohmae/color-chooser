/*
 * Copyright (c) 2024 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.util.ColorSaver
import net.mm2d.color.chooser.compose.util.toChooserColor

/**
 * ColorChooserView provides a color chooser.
 *
 * @param colorState The state of the color to be displayed. External updates are reflected immediately.
 * The state is normalized to 8-bit sRGB; [Color.Unspecified] is not supported.
 * @param modifier The modifier to be applied to the layout.
 * @param withAlpha Whether to edit alpha. If false, the displayed and stored color are opaque.
 * @param initialTab The initial tab to be displayed. Default is [Tab.PALETTE]. See [Tab].
 * @param tabs The tabs to be displayed. Default is [Tab.PALETTE], [Tab.HSV], [Tab.RGB]. See [Tab].
 * @param titleContentColor The color of the chooser tab labels, both selected and unselected.
 */
@Deprecated("Use ColorChooserScreen instead.")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorChooserView(
    colorState: MutableState<Color>,
    modifier: Modifier = Modifier,
    withAlpha: Boolean = true,
    initialTab: Tab = Tab.DEFAULT_TAB,
    tabs: List<Tab> = Tab.DEFAULT_TABS,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
) {
    val initialColor = rememberSaveable(saver = ColorSaver) { colorState.value.toChooserColor() }
    val currentColor = colorState.value.toChooserColor(withAlpha)
    SideEffect {
        if (colorState.value != currentColor) colorState.value = currentColor
    }
    ColorChooserContent(
        initialColor = initialColor.toChooserColor(withAlpha),
        currentColor = currentColor,
        onColorChanged = { colorState.value = it },
        modifier = modifier
            .padding(16.dp),
        withAlpha = withAlpha,
        choosers = tabs.map { it.toChooser() },
        initialChooser = initialTab.toChooser(),
        colors = ColorChooserDefaults.colors(
            selectedTabContentColor = titleContentColor,
            unselectedTabContentColor = titleContentColor,
        ),
    )
}

@PreviewLightDark
@Composable
private fun PreviewColorChooserScreen() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
    ) {
        Scaffold {
            ColorChooserView(
                colorState = remember { mutableStateOf(Color.Red) },
                modifier = Modifier
                    .padding(it),
            )
        }
    }
}
