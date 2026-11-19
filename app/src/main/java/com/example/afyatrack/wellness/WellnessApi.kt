
package com.example.afyatrack.wellness

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class WellnessResponse(
    val slip: WellnessSlip
)

data class WellnessSlip(
    val id: Int,
    val advice: String
)

interface WellnessApi {

    @GET("advice")
    suspend fun getWellnessTip(): WellnessResponse
}

object RetrofitClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.adviceslip.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: WellnessApi =
        retrofit.create(WellnessApi::class.java)
}
