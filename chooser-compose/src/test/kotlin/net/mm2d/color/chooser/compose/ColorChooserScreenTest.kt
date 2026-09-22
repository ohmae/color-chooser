/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ColorChooserScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun restoresEditedColorAlphaAndSelectedTabWithoutEmittingChange() {
        val restoration = StateRestorationTester(composeRule)
        var result = Color.Red
        var calls = 0
        restoration.setContent {
            MaterialTheme {
                ColorChooserScreen(
                    initialColor = Color.Red,
                    onColorChanged = {
                        result = it
                        calls++
                    },
                    modifier = Modifier
                        .width(320.dp)
                        .testTag("screen"),
                )
            }
        }
        // Initially only the alpha slider has a numeric label.
        clickSlider("255", 0.5f)
        composeRule.onNodeWithText("RGB").performClick()
        // Now the only 255 label is the red channel.
        clickSlider("255", 0.25f)
        val edited = composeRule.runOnIdle { result }
        val callsBeforeRestore = composeRule.runOnIdle { calls }
        assertNotEquals(Color.Red, edited)
        restoration.emulateSavedInstanceStateRestore()
        composeRule.onNodeWithText("#%08X".format(edited.toArgb())).assertExists()
        composeRule.onNodeWithText("RGB").assertIsSelected()
        composeRule.runOnIdle { assertEquals(callsBeforeRestore, calls) }
    }

    @Test
    @Suppress("DEPRECATION")
    fun dialogRestoresPreviewAndConfirmsTheSameColor() {
        var chosen: Color? = null
        var dismissed = false
        val content: @Composable () -> Unit = {
            MaterialTheme {
                ColorChooserDialog(
                    initialColor = Color.Red,
                    onDismissRequest = { dismissed = true },
                    onChooseColor = { chosen = it },
                    withAlpha = true,
                    modifier = Modifier.testTag("screen"),
                )
            }
        }
        composeRule.activityRule.scenario.onActivity { it.setContent(content = content) }
        clickSlider("255", 0.5f)
        composeRule.onNodeWithText("RGB").performClick()
        clickSlider("255", 0.25f)
        val editedText = composeRule.onAllNodesWithText("#", substring = true)[1]
            .fetchSemanticsNode().config[SemanticsProperties.Text].single().text
        // A Dialog has a separate composition; recreate its Activity and saveable registries too.
        composeRule.activityRule.scenario.recreate()
        composeRule.activityRule.scenario.onActivity { it.setContent(content = content) }
        composeRule.onNodeWithText(editedText).assertExists()
        composeRule.onNodeWithText("RGB").assertIsSelected()
        composeRule.onNodeWithText("OK").performClick()
        composeRule.runOnIdle {
            assertEquals(editedText, "#%08X".format(chosen!!.toArgb()))
            assertTrue(dismissed)
        }
    }

    @Test
    fun hsvUsesNewStateAfterInitialColorChanges() {
        var initialColor by mutableStateOf(Color.Red)
        var result = Color.Red
        composeRule.setContent {
            MaterialTheme {
                ColorChooserScreen(
                    initialColor = initialColor,
                    onColorChanged = { result = it },
                    choosers = listOf(Chooser.HSV),
                    withAlpha = false,
                    modifier = Modifier
                        .width(320.dp)
                        .testTag("screen"),
                )
            }
        }
        composeRule.runOnIdle { initialColor = Color.Blue }
        // Preview (64dp), spacing (12dp), then the center of the hue track (16dp).
        composeRule.onNodeWithTag("screen").performTouchInput {
            click(Offset(width / 2f, with(composeRule.density) { 92.dp.toPx() }))
        }
        val edited = composeRule.runOnIdle { result }
        assertNotEquals(Color.Blue, edited)
        composeRule.onNodeWithText("#%06X".format(edited.toArgb() and 0xFFFFFF)).assertExists()
    }

    @Test
    fun palettesCanBePlacedInScrollingColumn() {
        composeRule.setContent {
            MaterialTheme {
                Column(
                    Modifier
                        .width(320.dp)
                        .height(600.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    ColorChooserScreen(
                        initialColor = Color.Red,
                        onColorChanged = {},
                        choosers = listOf(Chooser.M2, Chooser.M3),
                        disableInnerScroll = true,
                    )
                }
            }
        }
        composeRule.onNodeWithText("M2").assertIsSelected()
        composeRule.onNodeWithText("M3").performClick().assertIsSelected()
    }

    @Test
    fun palettesCanBePlacedInLazyColumn() {
        composeRule.setContent {
            MaterialTheme {
                LazyColumn(
                    Modifier
                        .width(320.dp)
                        .height(600.dp),
                ) {
                    item {
                        ColorChooserScreen(
                            initialColor = Color.Red,
                            onColorChanged = {},
                            choosers = listOf(Chooser.M2, Chooser.M3),
                            disableInnerScroll = true,
                        )
                    }
                }
            }
        }
        composeRule.onNodeWithText("M2").assertIsSelected()
        composeRule.onNodeWithText("M3").performClick().assertIsSelected()
    }

    @Test
    fun statelessScreenReflectsExternalColorUpdatesImmediately() {
        var color by mutableStateOf(Color.Red)
        var changedColor = Color.Unspecified
        composeRule.setContent {
            MaterialTheme {
                ColorChooserScreen(
                    color = color,
                    onColorChanged = { changedColor = it },
                    initialColor = Color.Yellow,
                    modifier = Modifier
                        .width(320.dp)
                        .testTag("screen"),
                )
            }
        }
        composeRule.onNodeWithText("#FFFFFF00").assertExists()
        composeRule.onNodeWithText("#FFFF0000").assertExists()

        composeRule.runOnIdle { color = Color.Green }
        composeRule.onNodeWithText("#FF00FF00").assertExists()
    }

    @Test
    fun alphaTogglePreservesEditedRgbAndDoesNotRestoreTransparency() {
        var withAlpha by mutableStateOf(true)
        var result = Color.Unspecified
        composeRule.setContent {
            MaterialTheme {
                ColorChooserScreen(
                    initialColor = Color(0x40336699),
                    onColorChanged = { result = it },
                    withAlpha = withAlpha,
                    initialChooser = Chooser.RGB,
                    modifier = Modifier.width(320.dp),
                )
            }
        }
        composeRule.onNodeWithContentDescription("Red").performSemanticsAction(SemanticsActions.SetProgress) {
            it(18f)
        }
        composeRule.runOnIdle { withAlpha = false }
        composeRule.onNodeWithText("#126699").assertExists()
        composeRule.runOnIdle { withAlpha = true }
        composeRule.onNodeWithText("#FF126699").assertExists()
        composeRule.onNodeWithContentDescription("Green").performSemanticsAction(SemanticsActions.SetProgress) {
            it(52f)
        }
        composeRule.runOnIdle { assertEquals(Color(0xFF123499), result) }
    }

    @Test
    fun hsvBlackPreservesHueAndSaturationAcrossTabsAndRestoration() {
        val restoration = StateRestorationTester(composeRule)
        var result = Color.Blue
        restoration.setContent {
            MaterialTheme {
                ColorChooserScreen(
                    initialColor = Color.Blue,
                    onColorChanged = { result = it },
                    initialChooser = Chooser.HSV,
                    modifier = Modifier.width(320.dp),
                )
            }
        }
        composeRule.onNodeWithContentDescription("Saturation and brightness").performTouchInput {
            click(Offset(width.toFloat(), height.toFloat()) - Offset(1f, 1f))
        }
        composeRule.runOnIdle { assertEquals(Color.Black, result) }
        composeRule.onNodeWithText("RGB").performClick()
        restoration.emulateSavedInstanceStateRestore()
        composeRule.onNodeWithText("HSV").performClick()
        assertHue(240f)
        val increaseBrightness = composeRule.onNodeWithContentDescription("Saturation and brightness")
            .fetchSemanticsNode().config[SemanticsActions.CustomActions]
            .single { it.label == "Increase brightness" }.action
        composeRule.runOnIdle { increaseBrightness() }
        composeRule.runOnIdle { assertEquals(Color.hsv(240f, 1f, 0.01f), result) }
    }

    @Test
    fun hsvGrayPreservesHueEndpointAcrossTabsAndRestoration() {
        val restoration = StateRestorationTester(composeRule)
        restoration.setContent {
            MaterialTheme {
                ColorChooserScreen(
                    initialColor = Color.White,
                    onColorChanged = {},
                    initialChooser = Chooser.HSV,
                    modifier = Modifier.width(320.dp),
                )
            }
        }
        composeRule.onNodeWithContentDescription("Hue").performSemanticsAction(SemanticsActions.SetProgress) {
            it(360f)
        }
        composeRule.onNodeWithText("RGB").performClick()
        composeRule.onNodeWithText("HSV").performClick()
        assertHue(360f)
        restoration.emulateSavedInstanceStateRestore()
        assertHue(360f)
    }

    @Test
    fun hsvSynchronizesColorChangesMadeWhileItsTabIsHidden() {
        var color by mutableStateOf(Color.Blue)
        composeRule.setContent {
            MaterialTheme {
                ColorChooserScreen(
                    color = color,
                    onColorChanged = { color = it },
                    initialChooser = Chooser.HSV,
                    modifier = Modifier.width(320.dp),
                )
            }
        }
        composeRule.onNodeWithText("RGB").performClick()
        composeRule.runOnIdle { color = Color.Green }
        composeRule.onNodeWithText("HSV").performClick()
        assertHue(120f)
    }

    private fun assertHue(
        expected: Float,
    ) {
        val actual = composeRule.onNodeWithContentDescription("Hue")
            .fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo].current
        assertEquals(expected, actual, 0.001f)
    }

    private fun clickSlider(
        label: String,
        fraction: Float,
    ) {
        val labelBounds = composeRule.onNodeWithText(label).fetchSemanticsNode().boundsInRoot
        val screen = composeRule.onNodeWithTag("screen")
        val bounds = screen.fetchSemanticsNode().boundsInRoot
        screen.performTouchInput {
            click(Offset(width * fraction, labelBounds.center.y - bounds.top))
        }
    }
}
