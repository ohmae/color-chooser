/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ArrayRes
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mm2d.color.chooser.element.PaletteCell
import net.mm2d.color.chooser.util.toPixelsAsDp
import java.lang.ref.SoftReference

internal class PaletteView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RecyclerView(context, attrs, defStyleAttr),
    FlowCollector<Int> {
    private val delegate = ColorObserverDelegate(this)
    private val cellHeight = 48.toPixelsAsDp(context)
    private val cellAdapter = CellAdapter(context)
    private val linearLayoutManager = LinearLayoutManager(context)
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    init {
        val padding = resources.getDimensionPixelSize(R.dimen.mm2d_cc_palette_padding)
        setPadding(0, padding, 0, padding)
        clipToPadding = false
        setHasFixedSize(true)
        overScrollMode = OVER_SCROLL_NEVER
        itemAnimator = null
        layoutManager = linearLayoutManager
        isVerticalFadingEdgeEnabled = true
        setFadingEdgeLength(padding)
        adapter = cellAdapter
        cellAdapter.onColorChanged = {
            delegate.post(it)
        }
        coroutineScope.launch {
            cellAdapter.load()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        delegate.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        delegate.onDetachedFromWindow()
    }

    override fun isPaddingOffsetRequired(): Boolean = true
    override fun getTopPaddingOffset(): Int = -paddingTop
    override fun getBottomPaddingOffset(): Int = paddingBottom

    override suspend fun emit(
        value: Int,
    ) {
        cellAdapter.setColor(value)
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int,
    ) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (oldh == 0 && h > 0) {
            linearLayoutManager.scrollToPositionWithOffset(cellAdapter.index, (h - cellHeight) / 2)
        }
    }

    override fun onMeasure(
        widthSpec: Int,
        heightSpec: Int,
    ) {
        setMeasuredDimension(
            getDefaultSize(suggestedMinimumWidth, widthSpec),
            getDefaultSize(suggestedMinimumHeight, heightSpec),
        )
    }

    private class CellAdapter(
        private val context: Context,
    ) : Adapter<CellHolder>() {
        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var list: List<IntArray> = cache.get() ?: emptyList()
        private var color: Int = 0
        var onColorChanged: (color: Int) -> Unit = {}
        var index: Int = -1
            private set

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): CellHolder =
            CellHolder(inflater.inflate(R.layout.mm2d_cc_item_palette, parent, false))
                .also { holder -> holder.onColorChanged = onColorChanged }

        override fun onBindViewHolder(
            holder: CellHolder,
            position: Int,
        ) = holder.apply(list[position], color)

        override fun getItemCount(): Int = list.size

        suspend fun load() =
            withContext(Dispatchers.Main) {
                if (list.isNotEmpty()) return@withContext
                list = withContext(Dispatchers.IO) { createPalette(context) }
                notifyItemRangeInserted(0, list.size)
                index = list.indexOfFirst { it.contains(color) }
            }

        fun setColor(
            newColor: Int,
        ) {
            if (color == newColor) return
            color = newColor
            val newIndex = list.indexOfFirst { it.contains(newColor) }
            val lastIndex = index
            index = newIndex
            if (lastIndex >= 0) notifyItemChanged(lastIndex)
            if (newIndex >= 0) notifyItemChanged(newIndex)
        }
    }

    private class CellHolder(
        itemView: View,
    ) : ViewHolder(itemView) {
        private val viewList: List<PaletteCell> = (itemView as ViewGroup).children
            .map { it as PaletteCell }
            .toList()
        var onColorChanged: (color: Int) -> Unit = {}

        init {
            viewList.forEach {
                it.setOnClickListener(::performOnColorChanged)
            }
        }

        private fun performOnColorChanged(
            view: View,
        ) {
            onColorChanged.invoke(view.tag as? Int ?: return)
        }

        fun apply(
            colors: IntArray,
            selected: Int,
        ) {
            viewList.forEachIndexed { i, view ->
                val color = if (i < colors.size) colors[i] else Color.TRANSPARENT
                view.tag = color
                view.setColor(color)
                view.checked = color == selected
            }
        }
    }

    companion object {
        private var cache: SoftReference<List<IntArray>> = SoftReference<List<IntArray>>(null)

        @SuppressLint("Recycle")
        private fun <R> Resources.useTypedArray(
            @ArrayRes id: Int,
            block: TypedArray.() -> R,
        ): R = obtainTypedArray(id).use { it.block() }

        private fun createPalette(
            context: Context,
        ): List<IntArray> {
            cache.get()?.let { return it }
            val resources = context.resources
            return resources.useTypedArray(R.array.material_colors) {
                (0 until length()).map { resources.readColorArray(getResourceIdOrThrow(it)) }
            }.also {
                cache = SoftReference(it)
            }
        }

        private fun Resources.readColorArray(
            id: Int,
        ): IntArray = useTypedArray(id) { IntArray(length()) { getColorOrThrow(it) } }
    }
}
