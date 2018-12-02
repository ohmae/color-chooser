/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.colorchooser

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.core.content.res.use
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.mm2d.colorchooser.util.ColorUtils

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class PaletteView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), ColorChangeObserver {
    var observer: ColorChangeObserver? = null
    private val cellAdapter = CellAdapter(context)

    init {
        layoutManager = LinearLayoutManager(context)
        adapter = cellAdapter
        cellAdapter.onColorChanged = {
            observer?.onChange(it, this)
        }
    }

    override fun onChange(color: Int, notifier: Any?) {
        cellAdapter.setColor(color)
    }

    private class CellAdapter(context: Context) : Adapter<CellHolder>() {
        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private val list: List<IntArray> = createPalette(context)
        private var color: Int = 0
        var onColorChanged: ((color: Int) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellHolder {
            return CellHolder(inflater.inflate(R.layout.item_palette, parent, false)).also {
                it.onColorChanged = {
                    onColorChanged?.invoke(it)
                }
            }
        }

        override fun onBindViewHolder(holder: CellHolder, position: Int) {
            return holder.apply(list[position], color)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        fun setColor(color: Int) {
            this.color = color
            notifyDataSetChanged()
        }
    }

    private class CellHolder(itemView: View) : ViewHolder(itemView) {
        private val viewList: List<ImageView> = listOf(
            itemView.findViewById(R.id.sample_00),
            itemView.findViewById(R.id.sample_01),
            itemView.findViewById(R.id.sample_02),
            itemView.findViewById(R.id.sample_03),
            itemView.findViewById(R.id.sample_04),
            itemView.findViewById(R.id.sample_05),
            itemView.findViewById(R.id.sample_06),
            itemView.findViewById(R.id.sample_07),
            itemView.findViewById(R.id.sample_08),
            itemView.findViewById(R.id.sample_09),
            itemView.findViewById(R.id.sample_10),
            itemView.findViewById(R.id.sample_11),
            itemView.findViewById(R.id.sample_12),
            itemView.findViewById(R.id.sample_13)
        )
        var onColorChanged: ((color: Int) -> Unit)? = null
        init {
            viewList.forEach {
                it.setOnClickListener {
                    val color = it.tag as? Int ?: return@setOnClickListener
                    onColorChanged?.invoke(color)
                }
            }
        }

        fun apply(colors: IntArray, selected: Int) {
            for ((i, view) in viewList.withIndex()) {
                if (i < colors.size) {
                    val color = colors[i]
                    view.tag = color
                    view.setBackgroundColor(color)
                    if (color == selected) {
                        view.setImageResource(R.drawable.ic_check)
                        view.scaleType = ScaleType.CENTER
                        val whiteForeground = ColorUtils.shoulUseWhiteForeground(color)
                        val foregroundColor = if (whiteForeground) Color.WHITE else Color.BLACK
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            view.imageTintList = ColorStateList.valueOf(foregroundColor)
                        } else {
                            view.setColorFilter(foregroundColor)
                        }
                    } else {
                        view.setImageResource(0)
                    }
                    view.isEnabled = true
                } else {
                    view.tag = 0
                    view.setBackgroundColor(Color.TRANSPARENT)
                    view.setImageResource(0)
                    view.isEnabled = false
                }
            }
        }
    }

    companion object {
        @SuppressLint("Recycle")
        fun createPalette(context: Context): List<IntArray> {
            val result = mutableListOf<IntArray>()
            val resources = context.resources
            resources.obtainTypedArray(R.array.material_colors).use { ids ->
                for (i in 0 until ids.length()) {
                    val id = ids.getResourceId(i, 0)
                    if (id == 0) continue
                    resources.obtainTypedArray(id).use { colors ->
                        val array = IntArray(colors.length()).also { result.add(it) }
                        for (j in 0 until colors.length()) {
                            array[j] = colors.getColor(j, 0)
                        }
                    }
                }
            }
            return result
        }
    }
}
