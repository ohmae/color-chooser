package net.mm2d.color.chooser

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

internal class ColorObserverDelegate<T>(
    private val target: T,
) where T : View,
        T : FlowCollector<Int> {
    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    private var job: Job? = null
    private var colorFlow: MutableSharedFlow<Int>? = null

    fun onAttachedToWindow() {
        val owner = target.findColorStreamOwner()
            ?: throw IllegalStateException("parent is not ColorStreamOwner")
        val flow = owner.getColorStream()
        job = scope.launch {
            flow.distinctUntilChanged().collect(target)
        }
        colorFlow = flow
    }

    fun onDetachedFromWindow() {
        job?.cancel()
        job = null
        colorFlow = null
    }

    fun post(
        color: Int,
    ) {
        colorFlow?.tryEmit(color)
    }
}
