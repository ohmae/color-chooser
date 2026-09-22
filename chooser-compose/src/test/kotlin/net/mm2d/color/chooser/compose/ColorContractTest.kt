/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("DEPRECATION")
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ColorContractTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rgbEditsConvertedSrgbComponents() {
        val initial = Color(0.6f, 0.4f, 0.2f, 0.5f, ColorSpaces.DisplayP3)
        val expected = Color(initial.toArgb()).copy(red = 0f)
        var result = Color.Unspecified
        composeRule.setContent {
            MaterialTheme {
                ColorChooserScreen(initial, { result = it }, initialChooser = Chooser.RGB)
            }
        }
        setProgress("Red", 0f)
        composeRule.runOnIdle { assertEquals(expected, result) }
    }

    @Test
    fun hsvAcceptsExtendedColorSpaceInputAfterConversion() {
        val initial = Color(1.2f, -0.1f, 0.3f, 0.5f, ColorSpaces.ExtendedSrgb)
        var result = Color.Unspecified
        composeRule.setContent {
            MaterialTheme {
                ColorChooserScreen(initial, { result = it }, initialChooser = Chooser.HSV)
            }
        }
        setProgress("Hue", 180f)
        composeRule.runOnIdle {
            assertEquals(ColorSpaces.Srgb, result.colorSpace)
            assertEquals(Color(initial.toArgb()).alpha, result.alpha)
        }
    }

    @Test
    fun uneditedDialogReturnsOpaqueSrgbWhenAlphaIsDisabled() {
        val initial = Color(0.6f, 0.4f, 0.2f, 0.25f, ColorSpaces.DisplayP3)
        var result = Color.Unspecified
        composeRule.setContent {
            MaterialTheme {
                ColorChooserDialog(initial, {}, { result = it }, withAlpha = false)
            }
        }
        composeRule.onNodeWithText("OK").performClick()
        composeRule.runOnIdle { assertEquals(Color(initial.toArgb()).copy(alpha = 1f), result) }
    }

    @Test
    fun disablingAlphaKeepsEditedRgbAndDiscardsTransparency() {
        var withAlpha by mutableStateOf(true)
        var result = Color.Unspecified
        composeRule.setContent {
            MaterialTheme {
                ColorChooserDialog(
                    initialColor = Color(0x40336699),
                    onDismissRequest = {},
                    onChooseColor = { result = it },
                    withAlpha = withAlpha,
                    initialTab = Tab.RGB,
                    titleContentColor = Color.Magenta,
                )
            }
        }
        assertTextColor("RGB", Color.Magenta)
        assertTextColor("HSV", Color.Magenta)
        setProgress("Red", 18f)
        composeRule.runOnIdle { withAlpha = false }
        composeRule.runOnIdle { withAlpha = true }
        composeRule.onNodeWithText("#FF126699").assertExists()
        composeRule.onNodeWithText("OK").performClick()
        composeRule.runOnIdle { assertEquals(Color(0xFF126699), result) }
    }

    @Test
    fun modernDialogConfirmsColorOnOkClick() {
        val initial = Color.Blue
        var dismissed = false
        var confirmed = Color.Unspecified
        composeRule.setContent {
            MaterialTheme {
                ColorChooserDialog(
                    onDismissRequest = { dismissed = true },
                    onConfirm = { confirmed = it },
                    initialColor = initial,
                    withAlpha = false,
                )
            }
        }
        composeRule.onNodeWithText("OK").performClick()
        composeRule.runOnIdle {
            assertEquals(Color.Blue, confirmed)
            assertEquals(true, dismissed)
        }
    }

    @Test
    fun modernDialogSupportsCustomButtonsAndLiveUpdates() {
        var dismissed = false
        var confirmed = Color.Unspecified
        var changed = Color.Unspecified
        composeRule.setContent {
            MaterialTheme {
                ColorChooserDialog(
                    onDismissRequest = { dismissed = true },
                    onConfirm = { confirmed = it },
                    initialColor = Color.Red,
                    initialChooser = Chooser.RGB,
                    onColorChanged = { changed = it },
                    confirmButton = { selectedColor ->
                        androidx.compose.material3.TextButton(onClick = { confirmed = selectedColor }) {
                            androidx.compose.material3.Text("CustomSelect")
                        }
                    },
                )
            }
        }
        setProgress("Green", 255f)
        composeRule.runOnIdle {
            assertEquals(Color.Yellow, changed)
        }
        composeRule.onNodeWithText("CustomSelect").performClick()
        composeRule.runOnIdle {
            assertEquals(Color.Yellow, confirmed)
            assertEquals(false, dismissed)
        }
    }

    @Test
    fun legacyViewReflectsExternalResetAndWritesToLatestState() {
        val oldState = mutableStateOf(Color.Red)
        var state by mutableStateOf(oldState)
        composeRule.setContent {
            MaterialTheme {
                ColorChooserView(
                    colorState = state,
                    modifier = Modifier.width(320.dp),
                    initialTab = Tab.RGB,
                    titleContentColor = Color.Magenta,
                )
            }
        }
        assertTextColor("RGB", Color.Magenta)
        assertTextColor("HSV", Color.Magenta)
        setProgress("Blue", 255f)
        composeRule.runOnIdle { assertEquals(Color.Magenta, state.value) }
        composeRule.runOnIdle { state.value = Color.Red }
        composeRule.onAllNodesWithText("#FFFF0000").assertCountEquals(2)
        composeRule.onNodeWithText("RGB").assertIsSelected()
        composeRule.runOnIdle { state = mutableStateOf(Color.Green) }
        composeRule.onNodeWithText("#FF00FF00").assertExists()
        setProgress("Blue", 255f)
        composeRule.runOnIdle {
            assertEquals(Color.Cyan, state.value)
            assertEquals(Color.Red, oldState.value)
        }
    }

    @Test
    fun legacyViewNormalizesExternalColorEvenWithoutEditing() {
        val initial = Color(0.6f, 0.4f, 0.2f, 0.25f, ColorSpaces.DisplayP3)
        val state = mutableStateOf(initial)
        composeRule.setContent {
            MaterialTheme {
                ColorChooserView(state, withAlpha = false)
            }
        }
        composeRule.runOnIdle { assertEquals(Color(initial.toArgb()).copy(alpha = 1f), state.value) }
    }

    private fun assertTextColor(
        text: String,
        color: Color,
    ) {
        val layouts = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText(text, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(color, layouts.single().layoutInput.style.color)
    }

    private fun setProgress(
        label: String,
        value: Float,
    ) {
        composeRule.onNodeWithContentDescription(label).performSemanticsAction(SemanticsActions.SetProgress) {
            it(value)
        }
    }
}
