package com.pokemontcg.collector.domain.model

data class Card(
    val id: Long = 0,
    val tcgApiId: String,
    val name: String,
    val setId: String,
    val setName: String = "",
    val number: String,
    val imageUrl: String,
    val rarity: String?,
    val pokemonType: String?,
    val hp: Int?,
    val attacks: List<Attack> = emptyList(),
    val weaknesses: List<Weakness> = emptyList(),
    val notes: String = "",
    val inWishlist: Boolean = false,
    val forTrade: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)

data class Attack(
    val name: String,
    val damage: String,
    val text: String
)

data class Weakness(
    val type: String,
    val value: String
)
