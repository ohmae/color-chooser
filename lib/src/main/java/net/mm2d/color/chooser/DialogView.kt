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
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.graphics.alpha
import androidx.core.view.doOnLayout
import androidx.core.view.forEach
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import net.mm2d.color.chooser.databinding.Mm2dCcViewDialogBinding
import net.mm2d.color.chooser.util.toOpacity

internal class DialogView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ColorChangeMediator {
    private val liveData: MutableLiveData<Int> = MutableLiveData()
    private val binding: Mm2dCcViewDialogBinding
    val color: Int
        get() = binding.controlView.color

    init {
        orientation = VERTICAL
        binding = Mm2dCcViewDialogBinding.inflate(LayoutInflater.from(context), this)
    }

    fun init(color: Int, lifecycleOwner: LifecycleOwner) {
        onChangeColor(color.toOpacity())
        binding.controlView.setAlpha(color.alpha)
        val pageTitles: List<String> = listOf("palette", "hsv", "rgb")
        val pageViews: List<View> = listOf(
            PaletteView(context), HsvPage(context), SliderPage(context)
        )
        binding.viewPager.adapter = ViewPagerAdapter(pageViews, pageTitles)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        pageViews.forEach { observeRecursively(it, lifecycleOwner) }
        observeRecursively(binding.controlView, lifecycleOwner)
    }

    fun setCurrentItem(position: Int) {
        binding.viewPager.doOnLayout {
            binding.viewPager.setCurrentItem(position, false)
        }
    }

    private fun observeRecursively(view: View, lifecycleOwner: LifecycleOwner) {
        if (view is ColorObserver) liveData.observe(lifecycleOwner, view)
        (view as? ViewGroup)?.forEach { observeRecursively(it, lifecycleOwner) }
    }

    fun setWithAlpha(withAlpha: Boolean) {
        binding.controlView.setWithAlpha(withAlpha)
    }

    override fun onChangeColor(color: Int) {
        if (liveData.value == color) return
        liveData.value = color
    }
}
