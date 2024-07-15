package net.mm2d.color.chooser

/**
 * Enum class representing the color chooser tabs.
 */
enum class Tab {
    /**
     * Select from palette.
     */
    PALETTE,

    /**
     * Select by HSV.
     */
    HSV,

    /**
     * Select by RGB.
     */
    RGB,

    /**
     * Select from Material3 colors.
     */
    M3,
    ;

    companion object {
        val DEFAULT_TABS: List<Tab> = listOf(PALETTE, HSV, RGB)
        val DEFAULT_TAB: Tab = PALETTE
    }
}
