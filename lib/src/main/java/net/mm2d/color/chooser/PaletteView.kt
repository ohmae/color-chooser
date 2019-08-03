/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.mm2d.color.chooser.element.PaletteCell
import java.lang.ref.SoftReference
import kotlin.math.roundToInt

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
internal class PaletteView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), ColorChangeObserver {
    var observer: ColorChangeObserver? = null
    private val cellHeight = (48 * context.resources.displayMetrics.density).roundToInt()
    private val cellAdapter = CellAdapter(context)
    private val linearLayoutManager = LinearLayoutManager(context)

    init {
        val padding = context.resources.getDimensionPixelSize(R.dimen.mm2d_cc_palette_padding)
        setPadding(0, padding, 0, padding)
        setHasFixedSize(true)
        overScrollMode = View.OVER_SCROLL_NEVER
        clipToPadding = false
        itemAnimator = null
        layoutManager = linearLayoutManager
        adapter = cellAdapter
        cellAdapter.onColorChanged = {
            observer?.onChange(it, this)
        }
    }

    override fun onChange(color: Int, notifier: Any?) {
        cellAdapter.setColor(color)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (oldh == 0 && h > 0) {
            linearLayoutManager.scrollToPositionWithOffset(cellAdapter.index, (h - cellHeight) / 2)
        }
    }

    private class CellAdapter(context: Context) : Adapter<CellHolder>() {
        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private val list: List<IntArray> = createPalette(context)
        private var color: Int = 0
        private var checked: Boolean = false
        var onColorChanged: ((color: Int) -> Unit)? = null
        var index: Int = -1
            private set

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellHolder {
            return CellHolder(
                inflater.inflate(
                    R.layout.mm2d_cc_item_palette,
                    parent, false
                )
            ).also { holder ->
                holder.onColorChanged = { onColorChanged?.invoke(it) }
            }
        }

        override fun onBindViewHolder(holder: CellHolder, position: Int) =
            holder.apply(list[position], color)

        override fun getItemCount(): Int = list.size

        fun setColor(newColor: Int) {
            if (color == newColor) return
            color = newColor
            val newIndex = list.indexOfFirst { it.contains(newColor) }
            val lastIndex = index
            index = newIndex
            if (!checked && newIndex < 0) return
            checked = newIndex >= 0
            notifyItemChanged(lastIndex)
            if (lastIndex != newIndex) {
                notifyItemChanged(newIndex)
            }
        }
    }

    private class CellHolder(itemView: View) : ViewHolder(itemView) {
        private val viewList: List<PaletteCell> = (itemView as ViewGroup).children
            .map { it as PaletteCell }
            .toList()
        var onColorChanged: ((color: Int) -> Unit)? = null

        init {
            viewList.forEach {
                it.setOnClickListener(::performOnColorChanged)
            }
        }

        private fun performOnColorChanged(view: View) {
            onColorChanged?.invoke(view.tag as? Int ?: return)
        }

        fun apply(colors: IntArray, selected: Int) {
            viewList.withIndex().forEach { (i, view) ->
                val color = if (i < colors.size) colors[i] else Color.TRANSPARENT
                view.tag = color
                view.setColor(color)
                view.checked = color == selected
            }
        }
    }

    companion object {
        private var cache: SoftReference<List<IntArray>>? = null

        @SuppressLint("Recycle")
        private fun createPalette(context: Context): List<IntArray> {
            cache?.get()?.let { return it }
            val resources = context.resources
            return resources.obtainTypedArray(R.array.material_colors).use { ids ->
                Array(ids.length()) {
                    resources.obtainTypedArray(ids.getResourceIdOrThrow(it)).readColorArray()
                }.toList()
            }.also {
                cache = SoftReference(it)
            }
        }

        private fun TypedArray.readColorArray(): IntArray = use {
            IntArray(length()) { getColorOrThrow(it) }
        }
    }
}
