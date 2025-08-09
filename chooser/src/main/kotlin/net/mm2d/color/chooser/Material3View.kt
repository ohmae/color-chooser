/*
 * Copyright (c) 2023 大前良介 (OHMAE Ryosuke)
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
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.FlowCollector
import net.mm2d.color.chooser.util.resolveColor
import com.google.android.material.R as MR

internal class Material3View
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RecyclerView(context, attrs, defStyleAttr),
    FlowCollector<Int> {
    private val delegate = ColorObserverDelegate(this)
    private val cellAdapter = CellAdapter(context)
    private val linearLayoutManager = GridLayoutManager(context, 2)

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
    ) = Unit

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
        context: Context,
    ) : Adapter<CellHolder>() {
        private val inflater: LayoutInflater = LayoutInflater.from(context)
        var onColorChanged: (color: Int) -> Unit = {}

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): CellHolder =
            CellHolder(inflater.inflate(R.layout.mm2d_cc_item_material3, parent, false))
                .also { holder -> holder.onColorChanged = onColorChanged }

        override fun onBindViewHolder(
            holder: CellHolder,
            position: Int,
        ) {
            holder.apply(material3List[position])
        }

        override fun getItemCount(): Int = material3List.size
    }

    private class CellHolder(
        itemView: View,
    ) : ViewHolder(itemView) {
        private val context = itemView.context
        private val title: TextView = itemView.findViewById(R.id.name)
        var onColorChanged: (color: Int) -> Unit = {}

        fun apply(
            material3: Material3,
        ) {
            title.text = material3.name
            title.setTextColor(context.resolveColor(material3.textColor))
            val color = context.resolveColor(material3.color)
            itemView.setBackgroundColor(color)
            itemView.setOnClickListener {
                onColorChanged(color)
            }
        }
    }

    private data class Material3(
        val name: String,
        val color: Int,
        val textColor: Int,
    )

    companion object {
        private val material3List: List<Material3> = listOf(
            Material3(
                name = "Primary",
                color = MR.attr.colorPrimary,
                textColor = MR.attr.colorOnPrimary,
            ),
            Material3(
                name = "On Primary",
                color = MR.attr.colorOnPrimary,
                textColor = MR.attr.colorPrimary,
            ),
            Material3(
                name = "Primary Container",
                color = MR.attr.colorPrimaryContainer,
                textColor = MR.attr.colorOnPrimaryContainer,
            ),
            Material3(
                name = "On Primary Container",
                color = MR.attr.colorOnPrimaryContainer,
                textColor = MR.attr.colorPrimaryContainer,
            ),
            Material3(
                name = "Secondary",
                color = MR.attr.colorSecondary,
                textColor = MR.attr.colorOnSecondary,
            ),
            Material3(
                name = "On Secondary",
                color = MR.attr.colorOnSecondary,
                textColor = MR.attr.colorSecondary,
            ),
            Material3(
                name = "Secondary Container",
                color = MR.attr.colorSecondaryContainer,
                textColor = MR.attr.colorOnSecondaryContainer,
            ),
            Material3(
                name = "On Secondary Container",
                color = MR.attr.colorOnSecondaryContainer,
                textColor = MR.attr.colorSecondaryContainer,
            ),
            Material3(
                name = "Tertiary",
                color = MR.attr.colorTertiary,
                textColor = MR.attr.colorOnTertiary,
            ),
            Material3(
                name = "On Tertiary",
                color = MR.attr.colorOnTertiary,
                textColor = MR.attr.colorTertiary,
            ),
            Material3(
                name = "Tertiary Container",
                color = MR.attr.colorTertiaryContainer,
                textColor = MR.attr.colorOnTertiaryContainer,
            ),
            Material3(
                name = "On Tertiary Container",
                color = MR.attr.colorOnTertiaryContainer,
                textColor = MR.attr.colorTertiaryContainer,
            ),
            Material3(
                name = "Error",
                color = MR.attr.colorError,
                textColor = MR.attr.colorOnError,
            ),
            Material3(
                name = "On Error",
                color = MR.attr.colorOnError,
                textColor = MR.attr.colorError,
            ),
            Material3(
                name = "Error Container",
                color = MR.attr.colorErrorContainer,
                textColor = MR.attr.colorOnErrorContainer,
            ),
            Material3(
                name = "On Error Container",
                color = MR.attr.colorOnErrorContainer,
                textColor = MR.attr.colorErrorContainer,
            ),
            Material3(
                name = "Primary Fixed",
                color = MR.attr.colorPrimaryFixed,
                textColor = MR.attr.colorOnPrimaryFixed,
            ),
            Material3(
                name = "Primary Fixed Dim",
                color = MR.attr.colorPrimaryFixedDim,
                textColor = MR.attr.colorOnPrimaryFixedVariant,
            ),
            Material3(
                name = "On Primary Fixed",
                color = MR.attr.colorOnPrimaryFixed,
                textColor = MR.attr.colorPrimaryFixed,
            ),
            Material3(
                name = "On Primary Fixed Variant",
                color = MR.attr.colorOnPrimaryFixedVariant,
                textColor = MR.attr.colorPrimaryFixedDim,
            ),
            Material3(
                name = "Secondary Fixed",
                color = MR.attr.colorSecondaryFixed,
                textColor = MR.attr.colorOnSecondaryFixed,
            ),
            Material3(
                name = "Secondary Fixed Dim",
                color = MR.attr.colorSecondaryFixedDim,
                textColor = MR.attr.colorOnSecondaryFixedVariant,
            ),
            Material3(
                name = "On Secondary Fixed",
                color = MR.attr.colorOnSecondaryFixed,
                textColor = MR.attr.colorSecondaryFixed,
            ),
            Material3(
                name = "On Secondary Fixed Variant",
                color = MR.attr.colorOnSecondaryFixedVariant,
                textColor = MR.attr.colorSecondaryFixedDim,
            ),
            Material3(
                name = "Tertiary Fixed",
                color = MR.attr.colorTertiaryFixed,
                textColor = MR.attr.colorOnTertiaryFixed,
            ),
            Material3(
                name = "Tertiary Fixed Dim",
                color = MR.attr.colorTertiaryFixedDim,
                textColor = MR.attr.colorOnTertiaryFixedVariant,
            ),
            Material3(
                name = "On Tertiary Fixed",
                color = MR.attr.colorOnTertiaryFixed,
                textColor = MR.attr.colorTertiaryFixed,
            ),
            Material3(
                name = "On Tertiary Fixed Variant",
                color = MR.attr.colorOnTertiaryFixedVariant,
                textColor = MR.attr.colorTertiaryFixedDim,
            ),
            Material3(
                name = "Surface Dim",
                color = MR.attr.colorSurfaceDim,
                textColor = MR.attr.colorOnSurface,
            ),
            Material3(
                name = "Surface",
                color = MR.attr.colorSurface,
                textColor = MR.attr.colorOnSurface,
            ),
            Material3(
                name = "Surface Bright",
                color = MR.attr.colorSurfaceBright,
                textColor = MR.attr.colorOnSurface,
            ),
            Material3(
                name = "Surface Container Lowest",
                color = MR.attr.colorSurfaceContainerLowest,
                textColor = MR.attr.colorOnSurface,
            ),
            Material3(
                name = "Surface Container Low",
                color = MR.attr.colorSurfaceContainerLow,
                textColor = MR.attr.colorOnSurface,
            ),
            Material3(
                name = "Surface Container",
                color = MR.attr.colorSurfaceContainer,
                textColor = MR.attr.colorOnSurface,
            ),
            Material3(
                name = "Surface Container High",
                color = MR.attr.colorSurfaceContainerHigh,
                textColor = MR.attr.colorOnSurface,
            ),
            Material3(
                name = "Surface Container Highest",
                color = MR.attr.colorSurfaceContainerHighest,
                textColor = MR.attr.colorOnSurface,
            ),
            Material3(
                name = "On Surface",
                color = MR.attr.colorOnSurface,
                textColor = MR.attr.colorSurface,
            ),
            Material3(
                name = "On Surface Variant",
                color = MR.attr.colorOnSurfaceVariant,
                textColor = MR.attr.colorSurface,
            ),
            Material3(
                name = "Outline",
                color = MR.attr.colorOutline,
                textColor = MR.attr.colorOutlineVariant,
            ),
            Material3(
                name = "Outline Variant",
                color = MR.attr.colorOutlineVariant,
                textColor = MR.attr.colorOutline,
            ),
            Material3(
                name = "Inverse Surface",
                color = MR.attr.colorSurfaceInverse,
                textColor = MR.attr.colorOnSurfaceInverse,
            ),
            Material3(
                name = "Inverse On Surface",
                color = MR.attr.colorOnSurfaceInverse,
                textColor = MR.attr.colorSurfaceInverse,
            ),
            Material3(
                name = "Inverse Primary",
                color = MR.attr.colorPrimaryInverse,
                textColor = MR.attr.colorPrimary,
            ),
        )
    }
}
