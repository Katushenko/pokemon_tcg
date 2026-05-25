package com.pokemontcg.collector.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sets")
data class SetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "tcg_api_id") val tcgApiId: String,
    val name: String,
    val series: String,
    @ColumnInfo(name = "total_cards") val totalCards: Int,
    @ColumnInfo(name = "logo_url") val logoUrl: String? = null,
    @ColumnInfo(name = "release_date") val releaseDate: String
)
