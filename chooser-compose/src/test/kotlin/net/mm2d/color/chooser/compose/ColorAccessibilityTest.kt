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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ColorAccessibilityTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rgbAndAlphaCanBeEditedThroughSemanticsAndKeyboard() {
        var result = Color.Red
        composeRule.setContent {
            MaterialTheme {
                ColorChooserScreen(
                    initialColor = Color.Red,
                    onColorChanged = { result = it },
                    initialChooser = Chooser.RGB,
                    modifier = Modifier.width(320.dp),
                )
            }
        }
        setProgress("Red", 12f)
        setProgress("Green", 34f)
        setProgress("Blue", 56f)
        setProgress("Opacity", 78f)
        composeRule.runOnIdle { assertEquals(Color(12, 34, 56, 78), result) }
        val red = composeRule.onNodeWithContentDescription("Red")
        red.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        red.performKeyInput { pressKey(Key.DirectionRight) }
        composeRule.runOnIdle { assertEquals(Color(13, 34, 56, 78), result) }
        red.performKeyInput { pressKey(Key.MoveEnd) }
        composeRule.runOnIdle { assertEquals(Color(255, 34, 56, 78), result) }
    }

    @Test
    fun hsvCanBeEditedWithoutTouchCoordinates() {
        var color by mutableStateOf(Color.Red)
        composeRule.setContent {
            HsvChooser(color, { color = it }, Modifier.width(320.dp))
        }
        setProgress("Hue", 180f)
        composeRule.runOnIdle { assertEquals(Color.Cyan, color) }
        val sv = composeRule.onNodeWithContentDescription("Saturation and brightness")
        val decreaseSaturation = sv.fetchSemanticsNode().config[SemanticsActions.CustomActions]
            .single { it.label == "Decrease saturation" }.action
        composeRule.runOnIdle { decreaseSaturation() }
        val decreaseBrightness = sv.fetchSemanticsNode().config[SemanticsActions.CustomActions]
            .single { it.label == "Decrease brightness" }.action
        composeRule.runOnIdle { decreaseBrightness() }
        composeRule.runOnIdle { assertEquals(Color.hsv(180f, 0.99f, 0.99f), color) }
        sv.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        sv.performKeyInput { pressKey(Key.DirectionRight) }
        sv.performKeyInput { pressKey(Key.DirectionUp) }
        composeRule.runOnIdle { assertEquals(Color.Cyan, color) }
    }

    @Test
    fun paletteExposesColorAndSelection() {
        var color by mutableStateOf(Color.Red)
        composeRule.setContent {
            MaterialTheme {
                PaletteChooser(listOf(listOf(Color.Red, Color.Blue)), color, { color = it })
            }
        }
        composeRule.onNodeWithContentDescription("#FFFF0000").assertIsSelected()
        composeRule.onNodeWithContentDescription("#FF0000FF").performClick().assertIsSelected()
        composeRule.runOnIdle { assertEquals(Color.Blue, color) }
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
