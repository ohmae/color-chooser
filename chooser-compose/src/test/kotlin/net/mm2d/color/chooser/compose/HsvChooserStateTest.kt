/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.junit.Assert.assertEquals
import org.junit.Test

class HsvChooserStateTest {
    @Test
    fun acceptedEditKeepsFractionalCoordinatesAcrossSaving() {
        val state = HsvChooserState(Color.Red)
        val color = state.update(12.5f, 0.6f, 0.7f)
        state.syncColor(color)

        val restored = saveAndRestore(state)
        assertCoordinates(restored, 12.5f, 0.6f, 0.7f)
        assertEquals(Color(color.toArgb()), restored.editingColor)
        assertEquals(Color.hsv(12.5f, 0.6f, 0.8f), restored.update(restored.hue, restored.saturation, 0.8f))
    }

    @Test
    fun pendingEditIsNotSavedAndRejectionRestoresAcceptedBlackCoordinates() {
        val state = HsvChooserState(Color.Blue)
        state.syncColor(state.update(241.5f, 0.75f, 0f))
        state.update(120f, 1f, 0.5f)

        val restored = saveAndRestore(state)
        assertCoordinates(restored, 241.5f, 0.75f, 0f)
        assertEquals(Color.Black, restored.editingColor)

        state.syncColor(Color.Black)
        assertCoordinates(state, 241.5f, 0.75f, 0f)
        assertEquals(Color.hsv(241.5f, 0.75f, 0.1f), state.update(state.hue, state.saturation, 0.1f))
    }

    @Test
    fun editsThatKeepTheSameRgbAreSavedWithoutAnExternalColorUpdate() {
        val gray = HsvChooserState(Color.White)
        assertEquals(Color.White, gray.update(360f, 0f, 1f))
        assertCoordinates(saveAndRestore(gray), 360f, 0f, 1f)

        val black = HsvChooserState(Color.Black)
        assertEquals(Color.Black, black.update(120.5f, 0.75f, 0f))
        assertCoordinates(saveAndRestore(black), 120.5f, 0.75f, 0f)
    }

    @Test
    fun externalColorReplacesBothPendingAndAcceptedCoordinates() {
        val state = HsvChooserState(Color.Blue)
        state.update(120f, 1f, 1f)
        state.syncColor(Color.Cyan)
        assertCoordinates(state, 180f, 1f, 1f)
        assertCoordinates(saveAndRestore(state), 180f, 1f, 1f)

        state.update(60f, 1f, 1f)
        state.syncColor(Color.Cyan)
        assertCoordinates(state, 180f, 1f, 1f)
    }

    @Test
    fun restoresThePreviousFourElementSaveFormatWithHiddenBlackCoordinates() {
        val restored = HsvChooserState.Saver.restore(listOf(360f, 0.5f, 0f, Color.Black.toArgb()))!!
        assertCoordinates(restored, 360f, 0.5f, 0f)
        assertEquals(Color.Black, restored.editingColor)
        restored.syncColor(Color.Black)
        assertCoordinates(restored, 360f, 0.5f, 0f)
        assertEquals(Color.hsv(360f, 0.5f, 0.1f), restored.update(restored.hue, restored.saturation, 0.1f))
    }

    private fun saveAndRestore(
        state: HsvChooserState,
    ): HsvChooserState {
        val scope = object : SaverScope {
            override fun canBeSaved(
                value: Any,
            ): Boolean = true
        }
        val saved = with(HsvChooserState.Saver) { scope.save(state) }!!
        return HsvChooserState.Saver.restore(saved)!!
    }

    private fun assertCoordinates(
        state: HsvChooserState,
        hue: Float,
        saturation: Float,
        value: Float,
    ) {
        assertEquals(hue, state.hue, 0f)
        assertEquals(saturation, state.saturation, 0f)
        assertEquals(value, state.value, 0f)
    }
}
