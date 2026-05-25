package com.pokemontcg.collector.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "tcg_api_id") val tcgApiId: String,
    val name: String,
    @ColumnInfo(name = "set_id") val setId: String,
    @ColumnInfo(name = "set_name") val setName: String = "",
    val number: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    val rarity: String? = null,
    @ColumnInfo(name = "pokemon_type") val pokemonType: String? = null,
    val hp: Int? = null,
    val notes: String = "",
    @ColumnInfo(name = "in_wishlist") val inWishlist: Boolean = false,
    @ColumnInfo(name = "for_trade") val forTrade: Boolean = false,
    @ColumnInfo(name = "added_at") val addedAt: Long = System.currentTimeMillis()
)
