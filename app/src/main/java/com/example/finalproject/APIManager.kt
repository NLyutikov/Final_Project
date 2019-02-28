package com.example.finalproject

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

private const val BASE_URL = "https://www.themealdb.com/"
object APIManager {

    private val apiServise: APIServise by lazy(LazyThreadSafetyMode.NONE) {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIServise::class.java)
    }

    fun getRandomMeals(
        quantity: Int,
        onSuccess: ((LinkedList<Meal>) -> Unit),
        onFailure: (String) -> Unit
    ) {
        Thread {
            val list = LinkedList<Meal>()
            for (i in 0 until quantity) {
                val response = apiServise.getRandomMeal().execute()
                if (response.isSuccessful) {
                    response.body()?.forEach { meal ->
                        list.add(meal)
                    }
            } else {
                onFailure.invoke(response.errorBody()!!.string())
            }
        }
        onSuccess.invoke(list)
    }
}
}
