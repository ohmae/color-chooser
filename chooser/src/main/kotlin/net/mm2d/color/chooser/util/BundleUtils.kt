package net.mm2d.color.chooser.util

import android.os.Bundle
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun buildBundle(
    action: Bundle.() -> Unit,
): Bundle {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return Bundle().apply(action)
}
