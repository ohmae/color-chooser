/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.sample

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.mm2d.color.chooser.ColorChooserDialog
import net.mm2d.color.chooser.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ColorChooserDialog.Callback {
    private var color: Int = 0
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button1.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_CODE, color, initialTab = ColorChooserDialog.TAB_HSV)
        }
        binding.button2.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_CODE, color, true, initialTab = ColorChooserDialog.TAB_RGB)
        }
        binding.button3.setOnClickListener {
            startActivity(Intent(this, Main2Activity::class.java))
        }
        color = Color.parseColor("#B71C1C")
        binding.sample.setBackgroundColor(color)
    }

    override fun onColorChooserResult(requestCode: Int, resultCode: Int, color: Int) {
        if (requestCode != REQUEST_CODE || resultCode != Activity.RESULT_OK) return
        this.color = color
        binding.sample.setBackgroundColor(color)
    }

    companion object {
        private const val REQUEST_CODE = 10
    }
}
