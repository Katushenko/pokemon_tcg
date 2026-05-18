package com.pokemontcg.collector.data.remote

import com.pokemontcg.collector.data.remote.dto.CardsResponse
import com.pokemontcg.collector.data.remote.dto.SingleCardResponse
import com.pokemontcg.collector.data.remote.dto.SingleSetResponse
import com.pokemontcg.collector.data.remote.dto.SetsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonTcgApi {

    @GET("v2/cards")
    suspend fun getCards(
        @Query("q") query: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("orderBy") orderBy: String? = null
    ): CardsResponse

    @GET("v2/cards/{id}")
    suspend fun getCard(@Path("id") id: String): SingleCardResponse

    @GET("v2/sets")
    suspend fun getSets(
        @Query("orderBy") orderBy: String = "-releaseDate"
    ): SetsResponse

    @GET("v2/sets/{id}")
    suspend fun getSet(@Path("id") id: String): SingleSetResponse
}
