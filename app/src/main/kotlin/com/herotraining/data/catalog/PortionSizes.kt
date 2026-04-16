package com.herotraining.data.catalog

data class PortionSize(
    val id: String,   // S / M / L / XL / NONE
    val label: String,
    val desc: String,
    val kcal: Int,
    val untracked: Boolean = false
)

object PortionSizes {
    val ALL = listOf(
        PortionSize("S", "S", "небольшая", 200),
        PortionSize("M", "M", "обычная", 400),
        PortionSize("L", "L", "большая", 600),
        PortionSize("XL", "XL", "огромная", 800),
        PortionSize("NONE", "~", "без подсчёта", 0, untracked = true)
    )
}
