/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.sample

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import net.mm2d.color.chooser.ColorChooserDialog

class MainActivity : AppCompatActivity(), ColorChooserDialog.Callback {
    private var color: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_CODE, color)
        }
        color = ContextCompat.getColor(this, R.color.red_a700)
        sample.setBackgroundColor(color)
    }

    override fun onColorChooserResult(requestCode: Int, resultCode: Int, color: Int) {
        if (requestCode != REQUEST_CODE || resultCode != Activity.RESULT_OK) return
        this.color = color
        sample.setBackgroundColor(color)
    }

    companion object {
        private const val REQUEST_CODE = 10
    }
}
