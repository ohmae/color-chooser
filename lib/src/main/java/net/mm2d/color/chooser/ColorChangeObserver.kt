/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
internal interface ColorChangeObserver {
    fun onChange(color: Int, notifier: Any?)
}

internal fun colorChangeObserver(block: (color: Int, notifier: Any?) -> Unit) =
    object : ColorChangeObserver {
        override fun onChange(color: Int, notifier: Any?) {
            block(color, notifier)
        }
    }
