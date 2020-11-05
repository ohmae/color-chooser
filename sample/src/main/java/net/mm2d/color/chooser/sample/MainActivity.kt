/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.sample

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import net.mm2d.color.chooser.ColorChooserDialog
import net.mm2d.color.chooser.ColorChooserDialog.TAB_HSV
import net.mm2d.color.chooser.ColorChooserDialog.TAB_PALETTE
import net.mm2d.color.chooser.ColorChooserDialog.TAB_RGB
import net.mm2d.color.chooser.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ColorChooserDialog.Callback {
    private var color: Int = 0
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tabDefault.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_CODE, color)
        }
        binding.tabPalette.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_CODE, color, initialTab = TAB_PALETTE)
        }
        binding.tabHsv.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_CODE, color, initialTab = TAB_HSV)
        }
        binding.tabRgb.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_CODE, color, initialTab = TAB_RGB)
        }
        binding.tabDefaultAlpha.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_CODE, color, true)
        }
        binding.tabPaletteAlpha.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_CODE, color, true, TAB_PALETTE)
        }
        binding.tabHsvAlpha.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_CODE, color, true, TAB_HSV)
        }
        binding.tabRgbAlpha.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_CODE, color, true, TAB_RGB)
        }
        binding.lightTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.darkTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
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
