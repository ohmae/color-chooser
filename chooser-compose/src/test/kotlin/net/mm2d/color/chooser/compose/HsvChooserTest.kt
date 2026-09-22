/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class HsvChooserTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun controlsIgnoreTouchesWhenTheGripHasNoTravelRange() {
        var calls = 0
        composeRule.setContent {
            Column {
                HsvChooser(
                    currentColor = Color.Red,
                    onColorChanged = { calls++ },
                    modifier = Modifier
                        .size(16.dp, 56.dp)
                        .testTag("tinyHsv"),
                )
                ColorSlider(
                    value = 128,
                    onValueChange = { calls++ },
                    color = Color.Red,
                    accessibilityLabel = "Red",
                    labelColor = Color.Black,
                    labelStyle = TextStyle.Default,
                    modifier = Modifier
                        .size(52.dp, 32.dp)
                        .testTag("tinySlider"),
                )
            }
        }
        composeRule.onNodeWithTag("tinyHsv").performTouchInput {
            click(with(composeRule.density) { Offset(8.dp.toPx(), 16.dp.toPx()) })
            click(with(composeRule.density) { Offset(8.dp.toPx(), 48.dp.toPx()) })
        }
        composeRule.onNodeWithTag("tinySlider").performTouchInput {
            click(with(composeRule.density) { Offset(8.dp.toPx(), 16.dp.toPx()) })
        }
        composeRule.runOnIdle { assertEquals(0, calls) }
    }

    @Test
    fun smallHsvAreaCanReachBothSaturationAndValueEndpoints() {
        var color by mutableStateOf(Color.White)
        var direction by mutableStateOf(LayoutDirection.Ltr)
        composeRule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                HsvChooser(
                    currentColor = color,
                    onColorChanged = { color = it },
                    modifier = Modifier
                        .size(200.dp, 180.dp)
                        .testTag("hsv"),
                )
            }
        }
        for (layoutDirection in LayoutDirection.entries) {
            composeRule.runOnIdle {
                direction = layoutDirection
                color = Color.White
            }
            // 32dp hue + 8dp gap leaves a 140dp square, centered in the 200dp width.
            composeRule.onNodeWithTag("hsv").performTouchInput {
                click(with(composeRule.density) { Offset(162.dp.toPx(), 48.dp.toPx()) })
            }
            composeRule.runOnIdle { assertEquals(Color.Red, color) }
            composeRule.onNodeWithTag("hsv").performTouchInput {
                click(with(composeRule.density) { Offset(162.dp.toPx(), 172.dp.toPx()) })
            }
            composeRule.runOnIdle { assertEquals(Color.Black, color) }
        }
    }

    @Test
    fun hueAndSaturationValueUseLatestCallback() {
        var useLatestCallback by mutableStateOf(false)
        var color by mutableStateOf(Color.Red)
        var oldCalls = 0
        var newCalls = 0
        composeRule.setContent {
            MaterialTheme {
                HsvChooser(
                    currentColor = color,
                    onColorChanged = if (useLatestCallback) {
                        {
                            color = it
                            newCalls++
                        }
                    } else {
                        {
                            color = it
                            oldCalls++
                        }
                    },
                    modifier = Modifier
                        .size(320.dp, 400.dp)
                        .testTag("hsv"),
                )
            }
        }
        composeRule.runOnIdle { useLatestCallback = true }
        composeRule.onNodeWithTag("hsv").performTouchInput {
            click(Offset(width / 2f, with(composeRule.density) { 16.dp.toPx() }))
        }
        composeRule.runOnIdle {
            assertEquals(0, oldCalls)
            assertTrue(newCalls > 0)
            newCalls = 0
        }
        composeRule.onNodeWithTag("hsv").performTouchInput {
            click(Offset(width / 2f, with(composeRule.density) { 160.dp.toPx() }))
        }
        composeRule.runOnIdle {
            assertEquals(0, oldCalls)
            assertTrue(newCalls > 0)
        }
    }
}
