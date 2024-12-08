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
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import net.mm2d.color.chooser.ColorChooserDialog
import net.mm2d.color.chooser.ColorChooserDialog.TAB_HSV
import net.mm2d.color.chooser.ColorChooserDialog.TAB_MATERIAL3
import net.mm2d.color.chooser.ColorChooserDialog.TAB_PALETTE
import net.mm2d.color.chooser.ColorChooserDialog.TAB_RGB
import net.mm2d.color.chooser.sample.databinding.FragmentMain2Binding

class Main2Fragment : Fragment(R.layout.fragment_main2) {
    private var color: Int = Color.parseColor("#B71C1C")
    private lateinit var binding: FragmentMain2Binding

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
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
        binding.tabReorder1.setOnClickListener {
            ColorChooserDialog.show(
                this,
                REQUEST_KEY,
                color,
                tabs = intArrayOf(TAB_RGB, TAB_HSV, TAB_PALETTE, TAB_MATERIAL3),
            )
        }
        binding.tabReorder2.setOnClickListener {
            ColorChooserDialog.show(this, REQUEST_KEY, color, tabs = intArrayOf(TAB_HSV))
        }
        binding.lightTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.darkTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        binding.sample.setBackgroundColor(color)
        val context = requireContext()
        ColorChooserDialog.registerListener(this, REQUEST_KEY, {
            this.color = it
            binding.sample.setBackgroundColor(it)
            Toast.makeText(context, "onSelect #${"%08X".format(it)}", Toast.LENGTH_SHORT).show()
        }) {
            Toast.makeText(context, "onCancel", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_KEY = "REQUEST_KEY"
    }
}
