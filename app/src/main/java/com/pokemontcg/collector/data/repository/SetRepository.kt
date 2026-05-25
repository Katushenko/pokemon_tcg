package com.pokemontcg.collector.data.repository

import com.pokemontcg.collector.data.local.dao.SetDao
import com.pokemontcg.collector.data.local.entity.SetEntity
import com.pokemontcg.collector.data.remote.PokemonTcgApi
import com.pokemontcg.collector.data.remote.dto.SetDto
import com.pokemontcg.collector.domain.model.PokemonSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetRepository @Inject constructor(
    private val setDao: SetDao,
    private val api: PokemonTcgApi
) {

    fun getLocalSets(): Flow<List<PokemonSet>> =
        setDao.getAllSets().map { it.map(SetEntity::toDomain) }

    suspend fun fetchAndCacheSets(): List<PokemonSet> {
        val response = api.getSets()
        val entities = response.data.map { it.toEntity() }
        setDao.insertSets(entities)
        return entities.map { it.toDomain() }
    }

    suspend fun getSetByTcgId(tcgApiId: String): PokemonSet? =
        setDao.getSetByTcgId(tcgApiId)?.toDomain()
}

private fun SetEntity.toDomain(): PokemonSet = PokemonSet(
    id = id,
    tcgApiId = tcgApiId,
    name = name,
    series = series,
    totalCards = totalCards,
    logoUrl = logoUrl,
    releaseDate = releaseDate
)

private fun SetDto.toEntity(): SetEntity = SetEntity(
    tcgApiId = id,
    name = name,
    series = series,
    totalCards = total,
    logoUrl = images?.logo,
    releaseDate = releaseDate
)

private fun SetEntity.toDomain(collectedCount: Int = 0): PokemonSet = PokemonSet(
    id = id,
    tcgApiId = tcgApiId,
    name = name,
    series = series,
    totalCards = totalCards,
    logoUrl = logoUrl,
    releaseDate = releaseDate,
    collectedCount = collectedCount
)
