package com.pokemontcg.collector.data.local.dao

import androidx.room.*
import com.pokemontcg.collector.data.local.entity.CardTagEntity
import com.pokemontcg.collector.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN card_tags ct ON t.id = ct.tag_id
        WHERE ct.card_id = :cardId
    """)
    fun getTagsForCard(cardId: Long): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity): Long

    @Update
    suspend fun updateTag(tag: TagEntity)

    @Delete
    suspend fun deleteTag(tag: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardTag(cardTag: CardTagEntity)

    @Delete
    suspend fun deleteCardTag(cardTag: CardTagEntity)

    @Query("DELETE FROM card_tags WHERE card_id = :cardId AND tag_id = :tagId")
    suspend fun removeTagFromCard(cardId: Long, tagId: Long)
}
