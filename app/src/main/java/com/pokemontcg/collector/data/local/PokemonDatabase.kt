package com.pokemontcg.collector.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pokemontcg.collector.data.local.dao.CardDao
import com.pokemontcg.collector.data.local.dao.SetDao
import com.pokemontcg.collector.data.local.dao.TagDao
import com.pokemontcg.collector.data.local.entity.CardEntity
import com.pokemontcg.collector.data.local.entity.CardTagEntity
import com.pokemontcg.collector.data.local.entity.SetEntity
import com.pokemontcg.collector.data.local.entity.TagEntity

@Database(
    entities = [CardEntity::class, SetEntity::class, TagEntity::class, CardTagEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun setDao(): SetDao
    abstract fun tagDao(): TagDao
}
