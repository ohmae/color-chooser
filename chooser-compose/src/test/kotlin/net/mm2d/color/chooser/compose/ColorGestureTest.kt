/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ColorGestureTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val initialColor = Color(0x80336699)
    private val scrollState = ScrollState(0)
    private var color by mutableStateOf(initialColor)
    private var chooser by mutableStateOf(Chooser.RGB)
    private var calls = 0
    private var interceptInput = false
    private val barLabels = listOf("Red", "Green", "Blue", "Opacity", "Hue")

    @Test
    fun verticalSwipesOnEveryBarScrollTheParentWithoutEditing() {
        showChooser()
        barLabels.forEach { label ->
            selectControl(label)
            composeRule.onNodeWithContentDescription(label).performTouchInput {
                swipe(center, center - Offset(0f, 120f), durationMillis = 300)
            }
            composeRule.runOnIdle {
                assertTrue("$label must let the parent scroll", scrollState.value > 0)
                assertEquals("$label must not emit a color", 0, calls)
                assertEquals(initialColor, color)
            }
        }
    }

    @Test
    fun horizontalDragsOnEveryBarEditWithoutScrollingTheParent() {
        showChooser()
        barLabels.forEach { label ->
            selectControl(label)
            composeRule.onNodeWithContentDescription(label).performTouchInput {
                swipe(Offset(1f, centerY), Offset(width - 1f, centerY), durationMillis = 300)
            }
            composeRule.runOnIdle {
                assertTrue("$label must emit a color", calls > 0)
                assertEquals(0, scrollState.value)
                when (label) {
                    "Red" -> assertEquals(1f, color.red)
                    "Green" -> assertEquals(1f, color.green)
                    "Blue" -> assertEquals(1f, color.blue)
                    "Opacity" -> assertEquals(1f, color.alpha)
                    "Hue" -> assertTrue(color.red > color.green && color.green == color.blue)
                }
            }
        }
    }

    @Test
    fun canceledPressesDoNotEditAndFollowingTapsStillWork() {
        showChooser()
        barLabels.forEach { label ->
            selectControl(label)
            val control = composeRule.onNodeWithContentDescription(label)
            control.performTouchInput { down(center) }
            composeRule.runOnIdle { assertEquals(0, calls) }
            control.performTouchInput { cancel() }
            composeRule.runOnIdle { assertEquals(0, calls) }
            control.performTouchInput { click(center) }
            composeRule.runOnIdle { assertEquals(1, calls) }
        }
    }

    @Test
    fun consumedGesturesDoNotEditAnyControl() {
        showChooser()
        (barLabels + "Saturation and brightness").forEach { label ->
            selectControl(label)
            composeRule.runOnIdle { interceptInput = true }
            composeRule.onNodeWithContentDescription(label).performTouchInput {
                swipe(Offset(1f, 20f), Offset(width - 1f, 20f), durationMillis = 300)
            }
            composeRule.runOnIdle {
                assertEquals("$label must respect consumed input", 0, calls)
                interceptInput = false
            }
        }
    }

    @Test
    fun consumingAnActiveGestureStopsFurtherEdits() {
        showChooser()
        listOf("Red", "Hue", "Saturation and brightness").forEach { label ->
            selectControl(label)
            val control = composeRule.onNodeWithContentDescription(label)
            control.performTouchInput {
                down(Offset(1f, 20f))
                moveTo(Offset(width / 2f, 20f))
            }
            val callsBeforeInterception = composeRule.runOnIdle {
                assertTrue(calls > 0)
                interceptInput = true
                calls
            }
            control.performTouchInput {
                moveTo(Offset(width - 1f, 20f))
                up()
            }
            composeRule.runOnIdle {
                assertEquals(callsBeforeInterception, calls)
                interceptInput = false
            }
        }
    }

    @Test
    fun saturationValuePlaneKeepsVerticalDragsForColorEditing() {
        chooser = Chooser.HSV
        showChooser()
        composeRule.onNodeWithContentDescription("Saturation and brightness").performTouchInput {
            swipe(Offset(centerX, 100f), Offset(centerX, 1f), durationMillis = 300)
        }
        composeRule.runOnIdle {
            assertTrue(calls > 0)
            assertEquals(0, scrollState.value)
            assertEquals(1f, maxOf(color.red, color.green, color.blue))
        }
    }

    private fun selectControl(
        label: String,
    ) {
        composeRule.runOnIdle {
            runBlocking { scrollState.scrollTo(0) }
            chooser = if (label == "Hue" || label == "Saturation and brightness") Chooser.HSV else Chooser.RGB
            color = initialColor
            calls = 0
        }
    }

    private fun showChooser() {
        composeRule.setContent {
            MaterialTheme {
                Column(
                    Modifier
                        .size(320.dp, 500.dp)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    if (interceptInput) event.changes.forEach { it.consume() }
                                }
                            }
                        }
                        .verticalScroll(scrollState),
                ) {
                    Spacer(Modifier.height(100.dp))
                    ColorChooserScreen(
                        color = color,
                        onColorChanged = {
                            color = it
                            calls++
                        },
                        choosers = listOf(chooser),
                        disableInnerScroll = true,
                    )
                    Spacer(Modifier.height(600.dp))
                }
            }
        }
    }
}
