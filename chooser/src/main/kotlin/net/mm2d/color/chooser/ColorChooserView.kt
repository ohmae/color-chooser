/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.alpha
import androidx.core.view.doOnLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import net.mm2d.color.chooser.ColorChooserDialog.TAB_HSV
import net.mm2d.color.chooser.ColorChooserDialog.TAB_MATERIAL3
import net.mm2d.color.chooser.ColorChooserDialog.TAB_PALETTE
import net.mm2d.color.chooser.ColorChooserDialog.TAB_RGB
import net.mm2d.color.chooser.databinding.Mm2dCcViewDialogBinding
import net.mm2d.color.chooser.util.toOpacity

internal class ColorChooserView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr),
    ColorStreamOwner {
    private val colorFlow: MutableSharedFlow<Int> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val binding: Mm2dCcViewDialogBinding =
        Mm2dCcViewDialogBinding.inflate(LayoutInflater.from(context), this)
    val color: Int
        get() = binding.controlView.color
    private var tabs: IntArray = TABS

    fun init(
        color: Int,
        tabs: IntArray,
    ) {
        colorFlow.tryEmit(color.toOpacity())
        binding.controlView.setAlpha(color.alpha)
        val distinctTabs = tabs.filter { TABS.contains(it) }
            .distinct().toIntArray()
        val pageTitles: List<String> = distinctTabs.map {
            when (it) {
                TAB_PALETTE -> "PALETTE"
                TAB_HSV -> "HSV"
                TAB_MATERIAL3 -> "M3"
                else -> "RGB"
            }
        }
        binding.viewPager.adapter = ViewPagerAdapter(context, tabs)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = pageTitles[position]
        }.attach()
        this.tabs = distinctTabs
    }

    fun setCurrentItem(
        tab: Int,
    ) {
        binding.viewPager.doOnLayout {
            val index = tabs.indexOf(tab)
            if (index < 0) return@doOnLayout
            binding.viewPager.post {
                binding.viewPager.setCurrentItem(index, false)
            }
        }
    }

    fun getCurrentItem(): Int = binding.viewPager.currentItem

    fun setWithAlpha(
        withAlpha: Boolean,
    ) {
        binding.controlView.setWithAlpha(withAlpha)
    }

    override fun getColorStream(): MutableSharedFlow<Int> = colorFlow

    companion object {
        private val TABS = intArrayOf(TAB_PALETTE, TAB_HSV, TAB_RGB, TAB_MATERIAL3)
    }
}
