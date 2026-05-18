package com.pokemontcg.collector.domain.model

data class PokemonSet(
    val id: Long = 0,
    val tcgApiId: String,
    val name: String,
    val series: String,
    val totalCards: Int,
    val logoUrl: String?,
    val releaseDate: String,
    val collectedCount: Int = 0
)
