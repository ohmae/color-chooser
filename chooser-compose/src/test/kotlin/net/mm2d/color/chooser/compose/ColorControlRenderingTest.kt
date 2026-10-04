/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.roundToInt

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
                ColorSliderTrack(
                    value = 64,
                    onValueChange = {},
                    color = Color.Red,
                    accessibilityLabel = "Red",
                    modifier = Modifier.width(240.dp),
                )
            }
        }
        val slider = composeRule.onNodeWithContentDescription("Red")
        val before = slider.pixels()
        composeRule.runOnIdle { direction = LayoutDirection.Rtl }
        assertArrayEquals(before, slider.pixels())
    }

    @Test
    fun alphaSliderBadgesAndGripKeepPhysicalCoordinatesInRtl() {
        var direction by mutableStateOf(LayoutDirection.Ltr)
        var alphaValue by mutableIntStateOf(64)
        composeRule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                AlphaSlider(
                    value = alphaValue,
                    onValueChange = {},
                    color = Color.Red,
                    accessibilityLabel = "Alpha",
                    modifier = Modifier.width(240.dp),
                )
            }
        }
        val slider = composeRule.onNodeWithContentDescription("Alpha")
        val beforeNormal = slider.pixels()
        composeRule.runOnIdle { direction = LayoutDirection.Rtl }
        assertArrayEquals(beforeNormal, slider.pixels())

        composeRule.runOnIdle {
            direction = LayoutDirection.Ltr
            alphaValue = 220
        }
        val beforeEvading = slider.pixels()
        composeRule.runOnIdle { direction = LayoutDirection.Rtl }
        assertArrayEquals(beforeEvading, slider.pixels())
    }

    @Test
    fun sliderPressEmphasizesGripWithoutChangingValueAndCancelRestoresIt() {
        var callbackCount = 0
        composeRule.setContent {
            ColorSliderTrack(
                value = 128,
                onValueChange = { callbackCount++ },
                color = Color.Red,
                accessibilityLabel = "Red",
                modifier = Modifier.width(240.dp),
            )
        }
        val slider = composeRule.onNodeWithContentDescription("Red")
        val bounds = slider.fetchSemanticsNode().boundsInRoot
        val resting = slider.pixels()
        slider.performTouchInput { down(center) }
        assertTrue("Press must visibly emphasize the grip", !resting.contentEquals(slider.pixels()))
        assertTrue("Press must preserve the control dimensions", bounds == slider.fetchSemanticsNode().boundsInRoot)
        composeRule.runOnIdle { assertTrue("Press must not change the color", callbackCount == 0) }
        slider.performTouchInput { cancel() }
        assertArrayEquals("Cancelled gesture must clear the emphasis", resting, slider.pixels())
    }

    @Test
    fun keyboardFocusEmphasizesHueGripWithoutChangingColor() {
        var callbackCount = 0
        composeRule.setContent {
            HsvChooser(
                currentColor = Color.hsv(80f, 0.3f, 0.8f),
                onColorChanged = { callbackCount++ },
                modifier = Modifier.size(200.dp, 180.dp),
            )
        }
        val hue = composeRule.onNodeWithContentDescription("Hue")
        val resting = hue.pixels()
        hue.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        assertTrue("Focus must visibly emphasize the grip", !resting.contentEquals(hue.pixels()))
        composeRule.runOnIdle { assertTrue("Focus must not change the color", callbackCount == 0) }
    }

    @Test
    fun previewKeepsBothArgbCodesVisibleAtNarrowWidthWithDoubleFontScale() {
        composeRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(composeRule.density.density, 2f)) {
                val colors = ColorChooserDefaults.colors()
                ColorPreview(
                    initialColor = Color.Red,
                    resultColor = Color.Blue,
                    withAlpha = true,
                    labelColor = colors.previewLabelColor,
                    labelBackgroundColor = colors.previewLabelBackgroundColor,
                    labelTextStyle = ColorChooserDefaults.previewLabelStyle,
                    modifier = Modifier.size(240.dp, 64.dp),
                )
            }
        }
        listOf("#FFFF0000", "#FF0000FF").forEach { code ->
            val layouts = mutableListOf<TextLayoutResult>()
            composeRule.onNodeWithText(code).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
                it(layouts)
            }
            val layout = layouts.single()
            assertTrue("The full code must fit: $code", !layout.didOverflowWidth && !layout.didOverflowHeight)
        }
    }

    @Test
    fun screenDrawsHueAndSaturationValueGripsOutsideTheirControlBounds() {
        composeRule.setContent {
            Box(Modifier.size(320.dp, 640.dp).background(Color.Black).testTag("root")) {
                ColorChooserScreen(
                    initialColor = Color.Red,
                    onColorChanged = {},
                    withAlpha = false,
                    choosers = listOf(Chooser.HSV),
                    modifier = Modifier.offset(40.dp, 20.dp).width(240.dp).height(560.dp),
                )
            }
        }
        val hue = composeRule.onNodeWithContentDescription("Hue")
        hue.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        val hueBounds = hue.fetchSemanticsNode().boundsInRoot
        val huePixel = pixelAt(
            Offset(
                hueBounds.left + with(composeRule.density) { 8.dp.toPx() },
                hueBounds.top - with(composeRule.density) { 3.dp.toPx() },
            ),
        )
        assertTrue("The hue grip must extend above the panel", huePixel.red > 0.8f && huePixel.green < 0.3f)

        val sv = composeRule.onNodeWithContentDescription("Saturation and brightness")
        sv.performTouchInput { click(Offset(width - 1f, height - 1f)) }
        sv.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        val svBounds = sv.fetchSemanticsNode().boundsInRoot
        val points = listOf(
            Offset(
                svBounds.right + with(composeRule.density) { 12.dp.toPx() },
                svBounds.bottom - with(composeRule.density) { 8.dp.toPx() },
            ),
            Offset(
                svBounds.right - with(composeRule.density) { 8.dp.toPx() },
                svBounds.bottom + with(composeRule.density) { 20.dp.toPx() },
            ),
        )
        for (point in points) {
            val pixel = pixelAt(point)
            assertTrue("SV grip ticks must extend outside the panel: $point ($pixel)", pixel.red > 0.5f)
        }
    }

    @Test
    fun screenDrawsScaledRgbBadgeAndLastGripOutsideTheirControlBounds() {
        composeRule.setContent {
            Box(Modifier.size(320.dp, 640.dp).background(Color.Black).testTag("root")) {
                ColorChooserScreen(
                    initialColor = Color.White,
                    onColorChanged = {},
                    withAlpha = false,
                    choosers = listOf(Chooser.RGB),
                    sliderLabelStyle = ColorChooserDefaults.sliderLabelStyle.copy(fontSize = 32.sp, lineHeight = 40.sp),
                    modifier = Modifier.offset(40.dp, 20.dp).width(240.dp).height(240.dp),
                )
            }
        }
        composeRule.onNodeWithContentDescription("Red").performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        val badgeBounds = composeRule.onAllNodesWithText("255")[0].fetchSemanticsNode().boundsInRoot
        val badgePixel = pixelAt(
            Offset(badgeBounds.center.x, badgeBounds.top - with(composeRule.density) { 3.dp.toPx() }),
        )
        assertTrue("The scaled RGB badge must extend above the panel", badgePixel.red > 0.1f)
        composeRule.onNode(hasScrollAction()).performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 10_000f) }
        val blue = composeRule.onNodeWithContentDescription("Blue")
        blue.performSemanticsAction(SemanticsActions.RequestFocus) { it() }
        val blueBounds = blue.fetchSemanticsNode().boundsInRoot
        val bluePixel = pixelAt(
            Offset(
                blueBounds.right - with(composeRule.density) { 8.dp.toPx() },
                blueBounds.bottom + with(composeRule.density) { 2.dp.toPx() },
            ),
        )
        assertTrue("Last RGB grip must fit in scroll viewport: $blueBounds ($bluePixel)", bluePixel.blue > 0.8f)
    }

    private fun pixelAt(
        position: Offset,
    ): Color {
        val root = composeRule.onNodeWithTag("root")
        val bounds = root.fetchSemanticsNode().boundsInRoot
        val pixels = root.captureToImage().toPixelMap()
        return pixels[(position.x - bounds.left).roundToInt(), (position.y - bounds.top).roundToInt()]
    }

    private fun SemanticsNodeInteraction.pixels(): IntArray {
        val bitmap = captureToImage()
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.readPixels(pixels)
        assertTrue("The capture must contain the rendered gradient", pixels.distinct().size > 10)
        return pixels
    }
}
