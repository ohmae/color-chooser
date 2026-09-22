/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.sample.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.mm2d.color.chooser.compose.Chooser
import net.mm2d.color.chooser.compose.ColorChooserDialog

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

data class ChooserConfig(
    val withAlpha: Boolean = true,
    val choosers: List<Chooser> = Chooser.entries,
    val initialChooser: Chooser = Chooser.M2,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    currentColor: Color,
    currentThemeMode: ThemeMode,
    onColorChanged: (Color) -> Unit,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onOpenScreen: (ChooserConfig) -> Unit,
    modifier: Modifier = Modifier,
) {
    var withAlpha by rememberSaveable { mutableStateOf(true) }
    var initialChooser by rememberSaveable { mutableStateOf(Chooser.M2) }
    val selectedChoosers = remember { mutableStateListOf(Chooser.M2, Chooser.HSV, Chooser.RGB, Chooser.M3) }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ColorPreviewCard(color = currentColor)

                ConfigurationCard(
                    withAlpha = withAlpha,
                    onWithAlphaChanged = { withAlpha = it },
                    initialChooser = initialChooser,
                    onInitialModeChanged = { initialChooser = it },
                    selectedChoosers = selectedChoosers,
                    onToggleMode = { mode ->
                        if (selectedChoosers.contains(mode)) {
                            if (selectedChoosers.size > 1) {
                                selectedChoosers.remove(mode)
                                if (initialChooser == mode) {
                                    initialChooser = selectedChoosers.first()
                                }
                            }
                        } else {
                            selectedChoosers.add(mode)
                        }
                    },
                    onSetModes = { modes, defaultInitial ->
                        selectedChoosers.clear()
                        selectedChoosers.addAll(modes)
                        initialChooser = defaultInitial
                    },
                )

                ActionButtonsSection(
                    onOpenScreen = {
                        onOpenScreen(
                            ChooserConfig(
                                withAlpha = withAlpha,
                                choosers = selectedChoosers.toList(),
                                initialChooser = initialChooser,
                            ),
                        )
                    },
                    onOpenDialog = {
                        showDialog = true
                    },
                )

                QuickPresetsSection(
                    onSelectPreset = { preset ->
                        onOpenScreen(preset)
                    },
                )

                ThemeSelectionSection(
                    currentThemeMode = currentThemeMode,
                    onThemeModeChanged = onThemeModeChanged,
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showDialog) {
        val currentChoosers = selectedChoosers.toList().ifEmpty { listOf(Chooser.M2) }
        ColorChooserDialog(
            onDismissRequest = {
                showDialog = false
            },
            onConfirm = {
                onColorChanged(it)
                showDialog = false
            },
            initialColor = currentColor,
            withAlpha = withAlpha,
            choosers = currentChoosers,
            initialChooser = if (currentChoosers.contains(initialChooser)) {
                initialChooser
            } else {
                currentChoosers.first()
            },
        )
    }
}

@Composable
private fun ColorPreviewCard(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .checkerboardBackground()
                    .background(color)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Current Color",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "#%08X".format(color.toArgb()),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "ARGB: (%d, %d, %d, %d)".format(
                        (color.alpha * 255).toInt(),
                        (color.red * 255).toInt(),
                        (color.green * 255).toInt(),
                        (color.blue * 255).toInt(),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConfigurationCard(
    withAlpha: Boolean,
    onWithAlphaChanged: (Boolean) -> Unit,
    initialChooser: Chooser,
    onInitialModeChanged: (Chooser) -> Unit,
    selectedChoosers: List<Chooser>,
    onToggleMode: (Chooser) -> Unit,
    onSetModes: (List<Chooser>, Chooser) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Screen Configuration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "Alpha Control (withAlpha)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = if (withAlpha) "Alpha slider enabled" else "Alpha slider disabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = withAlpha,
                    onCheckedChange = onWithAlphaChanged,
                )
            }

            HorizontalDivider()

            Text(
                text = "Available Modes (modes)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Chooser.entries.forEach { mode ->
                    val isChecked = selectedChoosers.contains(mode)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleMode(mode) }
                            .padding(end = 8.dp),
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { onToggleMode(mode) },
                            enabled = !isChecked || selectedChoosers.size > 1,
                        )
                        Text(text = mode.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                FilterChip(
                    selected = selectedChoosers.size == Chooser.entries.size,
                    onClick = { onSetModes(Chooser.entries, Chooser.M2) },
                    label = { Text("All") },
                )
                FilterChip(
                    selected = selectedChoosers == listOf(Chooser.M2, Chooser.HSV, Chooser.RGB),
                    onClick = { onSetModes(listOf(Chooser.M2, Chooser.HSV, Chooser.RGB), Chooser.M2) },
                    label = { Text("M2+HSV+RGB") },
                )
                FilterChip(
                    selected = selectedChoosers == listOf(Chooser.RGB, Chooser.HSV),
                    onClick = { onSetModes(listOf(Chooser.RGB, Chooser.HSV), Chooser.RGB) },
                    label = { Text("RGB+HSV") },
                )
                FilterChip(
                    selected = selectedChoosers == listOf(Chooser.M3),
                    onClick = { onSetModes(listOf(Chooser.M3), Chooser.M3) },
                    label = { Text("M3 Only") },
                )
            }

            HorizontalDivider()

            Text(
                text = "Initial Mode (initialMode)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Chooser.entries.forEach { mode ->
                    FilterChip(
                        selected = initialChooser == mode,
                        onClick = { onInitialModeChanged(mode) },
                        label = { Text(mode.name) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsSection(
    onOpenScreen: () -> Unit,
    onOpenDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = onOpenScreen,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "OPEN COLOR CHOOSER SCREEN", fontSize = 15.sp)
        }

        OutlinedButton(
            onClick = onOpenDialog,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "OPEN DIALOG (DEPRECATED)")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickPresetsSection(
    onSelectPreset: (ChooserConfig) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Quick Presets (Open Screen)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Verify predefined configurations with a single tap:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        onSelectPreset(
                            ChooserConfig(
                                withAlpha = true,
                                choosers = Chooser.entries,
                                initialChooser = Chooser.M2,
                            ),
                        )
                    },
                ) {
                    Text("Default (All Modes)")
                }

                OutlinedButton(
                    onClick = {
                        onSelectPreset(
                            ChooserConfig(
                                withAlpha = false,
                                choosers = Chooser.entries,
                                initialChooser = Chooser.M2,
                            ),
                        )
                    },
                ) {
                    Text("Without Alpha")
                }

                OutlinedButton(
                    onClick = {
                        onSelectPreset(
                            ChooserConfig(
                                withAlpha = true,
                                choosers = listOf(Chooser.M3),
                                initialChooser = Chooser.M3,
                            ),
                        )
                    },
                ) {
                    Text("Single (M3)")
                }

                OutlinedButton(
                    onClick = {
                        onSelectPreset(
                            ChooserConfig(
                                withAlpha = true,
                                choosers = listOf(Chooser.RGB, Chooser.HSV, Chooser.M2, Chooser.M3),
                                initialChooser = Chooser.RGB,
                            ),
                        )
                    },
                ) {
                    Text("Reordered (RGB first)")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelectionSection(
    currentThemeMode: ThemeMode,
    onThemeModeChanged: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Theme Setting",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                ThemeMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = currentThemeMode == mode,
                        onClick = { onThemeModeChanged(mode) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = ThemeMode.entries.size,
                        ),
                    ) {
                        Text(text = mode.name)
                    }
                }
            }
        }
    }
}

private fun Modifier.checkerboardBackground(
    gridSize: Dp = 8.dp,
    color1: Color = Color.White,
    color2: Color = Color(0xFFE0E0E0),
): Modifier =
    drawBehind {
        val stepPx = gridSize.toPx()
        val cols = (size.width / stepPx).toInt() + 1
        val rows = (size.height / stepPx).toInt() + 1
        drawRect(color1)
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                if ((row + col) % 2 == 1) {
                    drawRect(
                        color = color2,
                        topLeft = Offset(col * stepPx, row * stepPx),
                        size = Size(stepPx, stepPx),
                    )
                }
            }
        }
    }
