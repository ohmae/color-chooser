/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.sample.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import net.mm2d.color.chooser.compose.Chooser
import net.mm2d.color.chooser.sample.compose.ui.theme.SampleTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(
        savedInstanceState: Bundle?,
    ) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleApp()
        }
    }
}

private sealed interface Screen {
    data object Main : Screen

    data class Chooser(
        val config: ChooserConfig,
    ) : Screen
}

private val ScreenSaver: Saver<Screen, String> = Saver(
    save = { screen ->
        when (screen) {
            is Screen.Main -> "main"

            is Screen.Chooser -> {
                val modesStr = screen.config.choosers.joinToString(",") { it.name }
                "chooser|${screen.config.withAlpha}|${screen.config.initialChooser.name}|$modesStr"
            }
        }
    },
    restore = { str ->
        if (str == "main") {
            Screen.Main
        } else if (str.startsWith("chooser|")) {
            val parts = str.split("|")
            val withAlpha = parts.getOrNull(1)?.toBooleanStrictOrNull() ?: true
            val initialChooser = parts.getOrNull(2)
                ?.let { runCatching { Chooser.valueOf(it) }.getOrNull() }
                ?: Chooser.M2
            val choosers = parts.getOrNull(3)
                ?.split(",")
                ?.mapNotNull { runCatching { Chooser.valueOf(it) }.getOrNull() }
                ?.ifEmpty { null }
                ?: Chooser.entries
            Screen.Chooser(
                ChooserConfig(
                    withAlpha = withAlpha,
                    choosers = choosers,
                    initialChooser = initialChooser,
                ),
            )
        } else {
            Screen.Main
        }
    },
)

@Composable
private fun SampleApp() {
    var colorInt by rememberSaveable { mutableIntStateOf(Color.Red.toArgb()) }
    val currentColor = Color(colorInt)
    var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
    var currentScreen by rememberSaveable(stateSaver = ScreenSaver) { mutableStateOf<Screen>(Screen.Main) }

    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    SampleTheme(darkTheme = isDark) {
        when (val screen = currentScreen) {
            is Screen.Main -> {
                MainScreen(
                    currentColor = currentColor,
                    currentThemeMode = themeMode,
                    onColorChanged = { colorInt = it.toArgb() },
                    onThemeModeChanged = { themeMode = it },
                    onOpenScreen = { config ->
                        currentScreen = Screen.Chooser(config)
                    },
                )
            }

            is Screen.Chooser -> {
                ChooserScreen(
                    initialColor = currentColor,
                    withAlpha = screen.config.withAlpha,
                    choosers = screen.config.choosers,
                    initialChooser = screen.config.initialChooser,
                    onColorSelected = { selectedColor ->
                        colorInt = selectedColor.toArgb()
                        currentScreen = Screen.Main
                    },
                    onCancel = {
                        currentScreen = Screen.Main
                    },
                )
            }
        }
    }
}
