package com.pokemontcg.collector.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "card_tags",
    primaryKeys = ["card_id", "tag_id"],
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["card_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CardTagEntity(
    @ColumnInfo(name = "card_id") val cardId: Long,
    @ColumnInfo(name = "tag_id") val tagId: Long
)
