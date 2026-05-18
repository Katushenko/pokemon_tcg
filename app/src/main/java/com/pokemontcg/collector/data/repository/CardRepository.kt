package com.pokemontcg.collector.data.repository

import com.pokemontcg.collector.data.local.dao.CardDao
import com.pokemontcg.collector.data.local.entity.CardEntity
import com.pokemontcg.collector.data.remote.PokemonTcgApi
import com.pokemontcg.collector.data.remote.dto.CardDto
import com.pokemontcg.collector.domain.model.Attack
import com.pokemontcg.collector.domain.model.Card
import com.pokemontcg.collector.domain.model.Weakness
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepository @Inject constructor(
    private val cardDao: CardDao,
    private val api: PokemonTcgApi
) {

    fun getCollectionCards(): Flow<List<Card>> =
        cardDao.getAllCards().map { it.map(CardEntity::toDomain) }

    fun searchCollectionCards(query: String): Flow<List<Card>> =
        cardDao.searchCards(query).map { it.map(CardEntity::toDomain) }

    fun getCardsBySet(setId: String): Flow<List<Card>> =
        cardDao.getCardsBySet(setId).map { it.map(CardEntity::toDomain) }

    fun getWishlistCards(): Flow<List<Card>> =
        cardDao.getWishlistCards().map { it.map(CardEntity::toDomain) }

    suspend fun getCardById(id: Long): Card? =
        cardDao.getCardById(id)?.toDomain()

    suspend fun addToCollection(card: Card): Long =
        cardDao.insertCard(card.toEntity())

    suspend fun updateCard(card: Card) =
        cardDao.updateCard(card.toEntity())

    suspend fun removeFromCollection(card: Card) =
        cardDao.deleteCard(card.toEntity())

    suspend fun updateNotes(cardId: Long, notes: String) =
        cardDao.updateNotes(cardId, notes)

    suspend fun updateWishlist(cardId: Long, inWishlist: Boolean) =
        cardDao.updateWishlist(cardId, inWishlist)

    suspend fun updateForTrade(cardId: Long, forTrade: Boolean) =
        cardDao.updateForTrade(cardId, forTrade)

    fun getCardCount(): Flow<Int> = cardDao.getCardCount()

    suspend fun searchApiCards(query: String): List<Card> {
        val response = api.getCards(query = query)
        return response.data.map { it.toDomain() }
    }

    suspend fun getApiCard(id: String): Card {
        val response = api.getCard(id)
        return response.data.toDomain()
    }

    suspend fun isCardInCollection(tcgApiId: String): Boolean =
        cardDao.getCardByTcgId(tcgApiId) != null

    suspend fun getCollectedCountForSet(setId: String): Int =
        cardDao.getCollectedCountForSet(setId)
}

private fun CardEntity.toDomain(): Card = Card(
    id = id,
    tcgApiId = tcgApiId,
    name = name,
    setId = setId,
    setName = setName,
    number = number,
    imageUrl = imageUrl,
    rarity = rarity,
    pokemonType = pokemonType,
    hp = hp,
    notes = notes,
    inWishlist = inWishlist,
    forTrade = forTrade,
    addedAt = addedAt
)

private fun Card.toEntity(): CardEntity = CardEntity(
    id = id,
    tcgApiId = tcgApiId,
    name = name,
    setId = setId,
    setName = setName,
    number = number,
    imageUrl = imageUrl,
    rarity = rarity,
    pokemonType = pokemonType,
    hp = hp,
    notes = notes,
    inWishlist = inWishlist,
    forTrade = forTrade,
    addedAt = addedAt
)

private fun CardDto.toDomain(): Card = Card(
    tcgApiId = id,
    name = name,
    setId = set.id,
    setName = set.name,
    number = number,
    imageUrl = images.large,
    rarity = rarity,
    pokemonType = types?.firstOrNull(),
    hp = hp?.toIntOrNull(),
    attacks = attacks?.map { Attack(it.name, it.damage ?: "", it.text ?: "") } ?: emptyList(),
    weaknesses = weaknesses?.map { Weakness(it.type, it.value) } ?: emptyList()
)
