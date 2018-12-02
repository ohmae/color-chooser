/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.colorchooser

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class ColorChooserDialog : DialogFragment() {
    private lateinit var dialogView: DialogView
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val color = arguments?.getInt(KEY_COLOR, 0) ?: 0
        dialogView = DialogView(activity!!)
        dialogView.onChange(color, null)
        return AlertDialog.Builder(activity!!, theme)
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                notifySelect()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()
    }

    override fun onCancel(dialog: DialogInterface?) {
        val requestCode = arguments?.getInt(KEY_REQUEST_CODE) ?: return
        extractCallback()?.let {
            it.onDialogResult(requestCode, Activity.RESULT_CANCELED, 0)
        }
    }

    private fun notifySelect() {
        val requestCode = arguments?.getInt(KEY_REQUEST_CODE) ?: return
        extractCallback()?.let {
            it.onDialogResult(requestCode, Activity.RESULT_OK, dialogView.color)
        }
    }

    private fun extractCallback(): Callback? {
        return targetFragment as? Callback ?: activity as? Callback
    }

    interface Callback {
        fun onDialogResult(requestCode: Int, resultCode: Int, color: Int)
    }

    companion object {
        private const val KEY_COLOR = "KEY_COLOR"
        private const val KEY_REQUEST_CODE = "KEY_REQUEST_CODE"
        const val TAG = "ColorChooserDialog"

        fun show(activity: FragmentActivity, requestCode: Int, color: Int) {
            val arguments = Bundle().apply {
                putInt(KEY_COLOR, color)
                putInt(KEY_REQUEST_CODE, requestCode)
            }
            ColorChooserDialog().let {
                it.arguments = arguments
                it.show(activity.supportFragmentManager, TAG)
            }
        }

        fun show(fragment: Fragment, requestCode: Int, color: Int) {
            val arguments = Bundle().apply {
                putInt(KEY_COLOR, color)
                putInt(KEY_REQUEST_CODE, requestCode)
            }
            ColorChooserDialog().let {
                it.setTargetFragment(fragment, requestCode)
                it.arguments = arguments
                it.show(fragment.fragmentManager, TAG)
            }
        }
    }
}
