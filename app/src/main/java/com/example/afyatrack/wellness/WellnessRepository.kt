
package com.example.afyatrack.wellness

class WellnessRepository {

    suspend fun getWellnessTip(): String {
        return try {
            val response = RetrofitClient.api.getWellnessTip()
            response.slip.advice
        } catch (e: Exception) {
            "Stay healthy! Drink enough water, eat well and exercise regularly."
        }
    }
}
