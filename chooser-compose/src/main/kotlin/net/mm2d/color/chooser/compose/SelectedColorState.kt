/*
 * Copyright (c) 2026 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import net.mm2d.color.chooser.compose.util.ColorSaver

// 正規化前の入力をキーにする。アルファの切り替えでは編集済み RGB をリセットしない。
@Composable
internal fun rememberSelectedColor(
    initialColorKey: Color,
    normalizedInitialColor: Color,
    withAlpha: Boolean,
): MutableState<Color> {
    val state = rememberSaveable(initialColorKey, stateSaver = ColorSaver) {
        mutableStateOf(normalizedInitialColor)
    }
    // 透明度は保存状態からも破棄し、再度有効にしても復活させない。変更通知は行わない。
    if (!withAlpha && state.value.alpha != 1f) state.value = state.value.copy(alpha = 1f)
    return state
}
