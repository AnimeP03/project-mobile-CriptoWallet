package com.example.criptowallet

import com.squareup.moshi.JsonClass
import retrofit2.http.GET

class Network {

    @JsonClass(generateAdapter = true)
    data class Coin(
        val name: String,
        val price: Double,
        val symbol: String
    )

    interface PriceApi{
        @GET("prices")
        suspend fun getPrices(): List<Coin>
    }
}