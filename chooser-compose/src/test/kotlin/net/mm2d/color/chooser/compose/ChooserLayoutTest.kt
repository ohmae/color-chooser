/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChooserLayoutTest {
    @Test
    fun fittingContentAndExactHeightBoundaryKeepTheFullSizeWithoutOuterScrolling() {
        for (chooser in Chooser.entries) {
            for (height in listOf(472.dp, 473.dp)) {
                val layout = calculate(chooser, height)
                assertFalse(layout.useEntireScroll)
                assertNull(layout.hsvAreaSize)
                assertFalse(layout.disablePaletteScroll)
            }
        }
    }

    @Test
    fun overflowingControlsScrollTogetherAndLeaveRoomOutsideTheHsvArea() {
        for (chooser in listOf(Chooser.RGB, Chooser.HSV)) {
            val layout = calculate(chooser, 471.dp)
            assertTrue(layout.useEntireScroll)
            assertEquals(235.5.dp, layout.hsvAreaSize)
            assertTrue(layout.disablePaletteScroll)
        }
    }

    @Test
    fun overflowingPalettesKeepTheirInnerScrollAndPrepareHsvSizeForTabTransitions() {
        for (chooser in listOf(Chooser.M2, Chooser.M3)) {
            val layout = calculate(chooser, 471.dp)
            assertFalse(layout.useEntireScroll)
            assertEquals(235.5.dp, layout.hsvAreaSize)
            assertFalse(layout.disablePaletteScroll)
        }
    }

    @Test
    fun entireScrollOptionUsesHalfHeightAndDisablesInnerScrollEvenWhenContentFits() {
        for (chooser in Chooser.entries) {
            for (height in listOf(240.dp, 600.dp)) {
                val layout = calculate(chooser, height, scrollEntireContent = true)
                assertTrue(layout.useEntireScroll)
                assertEquals(height / 2, layout.hsvAreaSize)
                assertTrue(layout.disablePaletteScroll)
            }
        }
    }

    @Test
    fun unboundedHeightKeepsFullHsvSizeAndDoesNotAddOuterScroll() {
        for (chooser in Chooser.entries) {
            for (scrollEntireContent in listOf(false, true)) {
                val layout = calculate(
                    chooser,
                    Dp.Infinity,
                    hasBoundedHeight = false,
                    scrollEntireContent = scrollEntireContent,
                )
                assertFalse(layout.useEntireScroll)
                assertNull(layout.hsvAreaSize)
                assertFalse(layout.disablePaletteScroll)
            }
        }
    }

    @Test
    fun disablingInnerScrollForAScrollingParentDoesNotForceOuterScrollOrResizeHsv() {
        for (height in listOf(600.dp, Dp.Infinity)) {
            val layout = calculate(
                Chooser.M2,
                height,
                hasBoundedHeight = height != Dp.Infinity,
                disableInnerScroll = true,
            )
            assertFalse(layout.useEntireScroll)
            assertNull(layout.hsvAreaSize)
            assertTrue(layout.disablePaletteScroll)
        }
    }

    private fun calculate(
        chooser: Chooser,
        height: Dp,
        hasBoundedHeight: Boolean = true,
        scrollEntireContent: Boolean = false,
        disableInnerScroll: Boolean = false,
    ): ChooserLayout =
        calculateChooserLayout(
            currentChooser = chooser,
            hasBoundedHeight = hasBoundedHeight,
            scrollEntireContent = scrollEntireContent,
            headerHeight = 100.dp,
            contentSpacing = 12.dp,
            maxWidth = 320.dp,
            maxHeight = height,
            disableInnerScroll = disableInnerScroll,
        )
}
