package net.mm2d.color.chooser

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

class ViewPagerAdapter(
    private val context: Context,
    private val tabs: IntArray,
) : RecyclerView.Adapter<PageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder =
        PageViewHolder(
            when (viewType) {
                ColorChooserDialog.TAB_PALETTE -> PaletteView(context)
                ColorChooserDialog.TAB_HSV -> HsvView(context)
                else -> SliderView(context)
            }.also {
                it.id = ViewCompat.generateViewId()
                it.layoutParams = RecyclerView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
                )
            }
        )

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) = Unit
    override fun getItemViewType(position: Int): Int = tabs[position]
    override fun getItemCount(): Int = tabs.size
}

class PageViewHolder(view: View) : RecyclerView.ViewHolder(view)
