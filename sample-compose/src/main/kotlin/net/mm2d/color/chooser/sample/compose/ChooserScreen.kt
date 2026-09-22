/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.sample.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import net.mm2d.color.chooser.compose.Chooser
import net.mm2d.color.chooser.compose.ColorChooserScreen

private val ColorSaver: Saver<Color, Int> = Saver(
    save = { it.toArgb() },
    restore = { Color(it) },
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooserScreen(
    initialColor: Color,
    withAlpha: Boolean,
    choosers: List<Chooser>,
    initialChooser: Chooser,
    onColorSelected: (Color) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onCancel)

    var selectedColor by rememberSaveable(initialColor, stateSaver = ColorSaver) {
        mutableStateOf(Color(initialColor.toArgb()))
    }
    if (!withAlpha && selectedColor.alpha != 1f) selectedColor = selectedColor.copy(alpha = 1f)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Color Chooser")
                },
                navigationIcon = {
                    TextButton(onClick = onCancel) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { onColorSelected(selectedColor) }) {
                        Text(
                            text = "Done",
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            ColorChooserScreen(
                initialColor = initialColor,
                onColorChanged = { selectedColor = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                withAlpha = withAlpha,
                choosers = choosers,
                initialChooser = initialChooser,
            )
        }
    }
}
