/*
 * Copyright (c) 2021 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.sample

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import net.mm2d.color.chooser.ColorChooserDialog
import net.mm2d.color.chooser.ColorChooserDialog.TAB_HSV
import net.mm2d.color.chooser.ColorChooserDialog.TAB_PALETTE
import net.mm2d.color.chooser.ColorChooserDialog.TAB_RGB
import net.mm2d.color.chooser.sample.databinding.FragmentMain2Binding

class Main2Fragment : Fragment(R.layout.fragment_main2) {
    private var color: Int = 0
    private lateinit var binding: FragmentMain2Binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMain2Binding.bind(view)
        binding.tabDefault.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_KEY, color)
        }
        binding.tabPalette.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_KEY, color, initialTab = TAB_PALETTE)
        }
        binding.tabHsv.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_KEY, color, initialTab = TAB_HSV)
        }
        binding.tabRgb.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_KEY, color, initialTab = TAB_RGB)
        }
        binding.tabDefaultAlpha.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_KEY, color, true)
        }
        binding.tabPaletteAlpha.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_KEY, color, true, TAB_PALETTE)
        }
        binding.tabHsvAlpha.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_KEY, color, true, TAB_HSV)
        }
        binding.tabRgbAlpha.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_KEY, color, true, TAB_RGB)
        }
        binding.lightTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.darkTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        color = Color.parseColor("#B71C1C")
        binding.sample.setBackgroundColor(color)
        ColorChooserDialog.registerListener(REQUEST_KEY, this) {
            this.color = it
            binding.sample.setBackgroundColor(it)
        }
    }

    companion object {
        private const val REQUEST_KEY = "REQUEST_KEY"
    }
}
