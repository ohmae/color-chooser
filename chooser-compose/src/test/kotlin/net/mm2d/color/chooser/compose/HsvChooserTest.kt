/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
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
