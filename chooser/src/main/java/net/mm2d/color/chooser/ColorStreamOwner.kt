package net.mm2d.color.chooser

import android.view.View
import kotlinx.coroutines.flow.MutableSharedFlow

internal interface ColorStreamOwner {
    fun getColorStream(): MutableSharedFlow<Int>
}

internal fun View.findColorStreamOwner(): ColorStreamOwner? {
    if (this is ColorStreamOwner) return this
    val parent = parent
    return if (parent !is View) null else parent.findColorStreamOwner()
}
