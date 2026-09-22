/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ColorControlRenderingTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun hsvGradientsAndGripsKeepPhysicalCoordinatesInRtl() {
        var direction by mutableStateOf(LayoutDirection.Ltr)
        composeRule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                HsvChooser(
                    currentColor = Color.hsv(80f, 0.3f, 0.8f),
                    onColorChanged = {},
                    modifier = Modifier
                        .size(200.dp, 180.dp)
                        .testTag("hsv"),
                )
            }
        }
        val before = composeRule.onNodeWithTag("hsv").pixels()
        composeRule.runOnIdle { direction = LayoutDirection.Rtl }
        assertArrayEquals(before, composeRule.onNodeWithTag("hsv").pixels())
    }

    @Test
    fun sliderGradientAndGripKeepPhysicalCoordinatesInRtl() {
        var direction by mutableStateOf(LayoutDirection.Ltr)
        composeRule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                ColorSlider(
                    value = 64,
                    onValueChange = {},
                    color = Color.Red,
                    accessibilityLabel = "Red",
                    labelColor = Color.Black,
                    labelStyle = TextStyle.Default,
                    // Keep the track at the same window coordinates to avoid rasterization differences.
                    modifier = Modifier
                        .width(240.dp)
                        .absoluteOffset {
                            IntOffset(if (direction == LayoutDirection.Rtl) (-36).dp.roundToPx() else 0, 0)
                        },
                )
            }
        }
        val before = composeRule.onNodeWithContentDescription("Red").pixels()
        composeRule.runOnIdle { direction = LayoutDirection.Rtl }
        assertArrayEquals(before, composeRule.onNodeWithContentDescription("Red").pixels())
    }

    private fun SemanticsNodeInteraction.pixels(): IntArray {
        val bitmap = captureToImage()
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.readPixels(pixels)
        assertTrue("The capture must contain the rendered gradient", pixels.distinct().size > 10)
        return pixels
    }
}
