/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Color chooser dialog
 */
object ColorChooserDialog {
    private const val KEY_INITIAL_COLOR = "KEY_INITIAL_COLOR"
    private const val KEY_REQUEST_CODE = "KEY_REQUEST_CODE"
    private const val KEY_WITH_ALPHA = "KEY_WITH_ALPHA"
    private const val KEY_INITIAL_TAB = "KEY_INITIAL_TAB"
    private const val TAG = "ColorChooserDialog"
    const val TAB_PALETTE: Int = 0
    const val TAB_HSV: Int = 1
    const val TAB_RGB: Int = 2

    /**
     * Result callback implements to Fragment or Activity
     */
    interface Callback {
        /**
         * Call at close dialog
         *
         * @param requestCode requestCode of show parameter
         * @param resultCode `Activity.RESULT_OK` or `Activity.RESULT_CANCELED`
         * @param color selected color
         */
        fun onColorChooserResult(requestCode: Int, resultCode: Int, color: Int)
    }

    /**
     * Show dialog
     *
     * @param activity FragmentActivity
     * @param requestCode use in listener call
     * @param initialColor initial color
     * @param withAlpha if true, alpha section is enabled
     * @param initialTab initial tab, TAB_PALETTE/TAB_HSV/TAB_RGB
     */
    fun show(
        activity: FragmentActivity,
        requestCode: Int = 0,
        initialColor: Int = Color.WHITE,
        withAlpha: Boolean = false,
        initialTab: Int = TAB_PALETTE
    ) {
        val fragmentManager = activity.supportFragmentManager
        if (fragmentManager.findFragmentByTag(TAG) != null) return
        if (fragmentManager.isStateSaved) return
        ColorChooserDialogImpl().also {
            it.arguments = bundleOf(
                KEY_INITIAL_COLOR to initialColor,
                KEY_REQUEST_CODE to requestCode,
                KEY_WITH_ALPHA to withAlpha,
                KEY_INITIAL_TAB to initialTab,
            )
        }.show(fragmentManager, TAG)
    }

    /**
     * Show dialog
     *
     * @param fragment Fragment
     * @param requestCode use in listener call
     * @param initialColor initial color
     * @param withAlpha if true, alpha section is enabled
     * @param initialTab initial tab, TAB_PALETTE/TAB_HSV/TAB_RGB
     */
    fun show(
        fragment: Fragment,
        requestCode: Int = 0,
        initialColor: Int = Color.WHITE,
        withAlpha: Boolean = false,
        initialTab: Int = TAB_PALETTE
    ) {
        val fragmentManager = fragment.childFragmentManager
        if (fragmentManager.findFragmentByTag(TAG) != null || fragmentManager.isStateSaved) {
            return
        }
        val arguments = bundleOf(
            KEY_INITIAL_COLOR to initialColor,
            KEY_REQUEST_CODE to requestCode,
            KEY_WITH_ALPHA to withAlpha,
            KEY_INITIAL_TAB to initialTab,
        )
        ColorChooserDialogImpl().also {
            it.arguments = arguments
        }.show(fragmentManager, TAG)
    }

    internal class ColorChooserDialogImpl : DialogFragment() {
        private lateinit var dialogView: DialogView

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val activity = requireActivity()
            dialogView = DialogView(activity)
            val tab = requireArguments().getInt(KEY_INITIAL_TAB, 0)
            dialogView.setCurrentItem(tab)
            val color = requireArguments().getInt(KEY_INITIAL_COLOR, 0)
            dialogView.init(color, this)
            dialogView.setWithAlpha(requireArguments().getBoolean(KEY_WITH_ALPHA))
            return AlertDialog.Builder(activity)
                .setView(dialogView)
                .setPositiveButton("OK") { _, _ ->
                    notifySelect()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .create()
        }

        override fun onCancel(dialog: DialogInterface) {
            val requestCode = requireArguments().getInt(KEY_REQUEST_CODE)
            extractCallback()?.onColorChooserResult(requestCode, Activity.RESULT_CANCELED, 0)
        }

        private fun notifySelect() {
            val requestCode = requireArguments().getInt(KEY_REQUEST_CODE)
            extractCallback()?.onColorChooserResult(
                requestCode,
                Activity.RESULT_OK,
                dialogView.color
            )
        }

        private fun extractCallback(): Callback? {
            return parentFragment as? Callback ?: activity as? Callback
        }
    }
}
