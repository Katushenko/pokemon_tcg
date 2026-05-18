package com.pokemontcg.collector.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CardsResponse(
    @SerializedName("data") val data: List<CardDto>,
    @SerializedName("page") val page: Int = 1,
    @SerializedName("pageSize") val pageSize: Int = 20,
    @SerializedName("count") val count: Int = 0,
    @SerializedName("totalCount") val totalCount: Int = 0
)

data class SingleCardResponse(
    @SerializedName("data") val data: CardDto
)

data class CardDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("number") val number: String,
    @SerializedName("set") val set: SetRefDto,
    @SerializedName("images") val images: CardImagesDto,
    @SerializedName("rarity") val rarity: String?,
    @SerializedName("types") val types: List<String>?,
    @SerializedName("hp") val hp: String?,
    @SerializedName("attacks") val attacks: List<AttackDto>?,
    @SerializedName("weaknesses") val weaknesses: List<WeaknessDto>?
)

data class SetRefDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class CardImagesDto(
    @SerializedName("small") val small: String,
    @SerializedName("large") val large: String
)

data class AttackDto(
    @SerializedName("name") val name: String,
    @SerializedName("damage") val damage: String?,
    @SerializedName("text") val text: String?
)

data class WeaknessDto(
    @SerializedName("type") val type: String,
    @SerializedName("value") val value: String
)
