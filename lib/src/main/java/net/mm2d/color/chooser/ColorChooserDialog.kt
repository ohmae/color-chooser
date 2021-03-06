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
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import net.mm2d.color.chooser.databinding.Mm2dCcDialogBinding

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
        show(
            activity.supportFragmentManager,
            requestCode,
            initialColor,
            withAlpha,
            initialTab
        )
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
        show(
            fragment.childFragmentManager,
            requestCode,
            initialColor,
            withAlpha,
            initialTab
        )
    }

    private fun show(
        fragmentManager: FragmentManager,
        requestCode: Int = 0,
        initialColor: Int = Color.WHITE,
        withAlpha: Boolean = false,
        initialTab: Int = TAB_PALETTE
    ) {
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

    internal class ColorChooserDialogImpl : DialogFragment() {
        private var _dialogView: DialogView? = null
        private val dialogView: DialogView
            get() = _dialogView ?: throw IllegalStateException()

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val activity = requireActivity()
            _dialogView = Mm2dCcDialogBinding.inflate(LayoutInflater.from(activity)).root

            if (savedInstanceState != null) {
                val tab = savedInstanceState.getInt(KEY_INITIAL_TAB, 0)
                dialogView.setCurrentItem(tab)
                val color = savedInstanceState.getInt(KEY_INITIAL_COLOR, 0)
                dialogView.init(color, this)
            } else {
                val arguments = requireArguments()
                val tab = arguments.getInt(KEY_INITIAL_TAB, 0)
                dialogView.setCurrentItem(tab)
                val color = arguments.getInt(KEY_INITIAL_COLOR, 0)
                dialogView.init(color, this)
            }
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

        override fun onDestroyView() {
            super.onDestroyView()
            _dialogView = null
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putInt(KEY_INITIAL_TAB, dialogView.getCurrentItem())
            outState.putInt(KEY_INITIAL_COLOR, dialogView.color)
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
