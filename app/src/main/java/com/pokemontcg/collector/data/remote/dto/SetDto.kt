package com.pokemontcg.collector.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SetsResponse(
    @SerializedName("data") val data: List<SetDto>
)

data class SingleSetResponse(
    @SerializedName("data") val data: SetDto
)

data class SetDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("series") val series: String,
    @SerializedName("total") val total: Int,
    @SerializedName("images") val images: SetImagesDto?,
    @SerializedName("releaseDate") val releaseDate: String
)

data class SetImagesDto(
    @SerializedName("symbol") val symbol: String?,
    @SerializedName("logo") val logo: String?
)
