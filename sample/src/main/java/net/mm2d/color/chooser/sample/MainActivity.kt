/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser.sample

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.mm2d.color.chooser.ColorChooserDialog
import net.mm2d.color.chooser.ColorChooserDialog.TAB_HSV
import net.mm2d.color.chooser.ColorChooserDialog.TAB_MATERIAL3
import net.mm2d.color.chooser.ColorChooserDialog.TAB_PALETTE
import net.mm2d.color.chooser.ColorChooserDialog.TAB_RGB
import net.mm2d.color.chooser.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var color: Int = Color.parseColor("#B71C1C")
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(
        savedInstanceState: Bundle?,
    ) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            ColorChooserDialog.show(this, REQUEST_KEY, color, tabs = intArrayOf(TAB_RGB, TAB_HSV))
        }
        binding.lightTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.darkTheme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        binding.next.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }
        binding.sample.setBackgroundColor(color)
        ColorChooserDialog.registerListener(this, REQUEST_KEY, {
            this.color = it
            binding.sample.setBackgroundColor(it)
            Toast.makeText(this, "onSelect #${"%08X".format(it)}", Toast.LENGTH_SHORT).show()
        }) {
            Toast.makeText(this, "onCancel", Toast.LENGTH_SHORT).show()
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    companion object {
        private const val REQUEST_KEY = "REQUEST_KEY"
    }
}
