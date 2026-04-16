package com.herotraining.domain.calc

import com.herotraining.data.model.Hero

data class RankInfo(
    val rank: String,
    val label: String,
    val index: Int,
    val progress: Float, // 0f..1f
    val nextRank: String?
)

fun getCurrentRank(hero: Hero, rp: Int): RankInfo {
    val sys = hero.rankSystem
    var idx = 0
    for (i in sys.ranks.indices.reversed()) {
        if (rp >= sys.thresholds[i]) { idx = i; break }
    }
    val isMax = idx == sys.ranks.lastIndex
    val base = sys.thresholds[idx]
    val next = if (!isMax) sys.thresholds[idx + 1] else base
    val progress = if (!isMax) ((rp - base).toFloat() / (next - base).coerceAtLeast(1)).coerceIn(0f, 1f) else 1f
    return RankInfo(
        rank = sys.ranks[idx],
        label = sys.labels?.getOrNull(idx) ?: sys.ranks[idx],
        index = idx,
        progress = progress,
        nextRank = if (isMax) null else sys.ranks[idx + 1]
    )
}
