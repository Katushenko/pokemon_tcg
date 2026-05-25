package com.pokemontcg.collector.data.local.dao

import androidx.room.*
import com.pokemontcg.collector.data.local.entity.SetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {

    @Query("SELECT * FROM sets ORDER BY release_date DESC")
    fun getAllSets(): Flow<List<SetEntity>>

    @Query("SELECT * FROM sets WHERE tcg_api_id = :tcgApiId")
    suspend fun getSetByTcgId(tcgApiId: String): SetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<SetEntity>)

    @Delete
    suspend fun deleteSet(set: SetEntity)

    @Query("SELECT COUNT(*) FROM sets")
    fun getSetCount(): Flow<Int>
}
