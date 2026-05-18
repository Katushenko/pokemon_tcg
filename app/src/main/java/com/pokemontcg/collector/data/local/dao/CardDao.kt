package com.pokemontcg.collector.data.local.dao

import androidx.room.*
import com.pokemontcg.collector.data.local.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM cards ORDER BY added_at DESC")
    fun getAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: Long): CardEntity?

    @Query("SELECT * FROM cards WHERE tcg_api_id = :tcgApiId")
    suspend fun getCardByTcgId(tcgApiId: String): CardEntity?

    @Query("SELECT * FROM cards WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCards(query: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE set_id = :setId ORDER BY number ASC")
    fun getCardsBySet(setId: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE in_wishlist = 1 ORDER BY added_at DESC")
    fun getWishlistCards(): Flow<List<CardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity): Long

    @Update
    suspend fun updateCard(card: CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Query("UPDATE cards SET notes = :notes WHERE id = :cardId")
    suspend fun updateNotes(cardId: Long, notes: String)

    @Query("UPDATE cards SET in_wishlist = :inWishlist WHERE id = :cardId")
    suspend fun updateWishlist(cardId: Long, inWishlist: Boolean)

    @Query("UPDATE cards SET for_trade = :forTrade WHERE id = :cardId")
    suspend fun updateForTrade(cardId: Long, forTrade: Boolean)

    @Query("SELECT COUNT(*) FROM cards")
    fun getCardCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cards WHERE set_id = :setId")
    suspend fun getCollectedCountForSet(setId: String): Int
}
