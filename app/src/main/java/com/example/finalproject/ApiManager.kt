package com.example.finalproject

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://www.themealdb.com/"

object ApiManager {

    private val apiServise: ApiService by lazy(LazyThreadSafetyMode.NONE) {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun getRandomMeals(
        onSuccess: ((List<MealNetwork>) -> Unit),
        onFailure: (String) -> Unit
    ) {
        apiServise.getRandomMeal().enqueue(object : Callback<MealsNetwork> {
            override fun onFailure(call: Call<MealsNetwork>, t: Throwable) {
                onFailure.invoke(t.toString())
            }

            override fun onResponse(call: Call<MealsNetwork>, response: Response<MealsNetwork>) {
                onSuccess.invoke(response.body()!!.meals)
            }
        })
    }

    fun getSearchMeals(
        text: String,
        onSuccess: (List<MealNetwork>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        apiServise.getSearchMeal(text).enqueue(object : Callback<MealsNetwork> {
            override fun onFailure(call: Call<MealsNetwork>, t: Throwable) {
                onFailure.invoke(t.toString())
            }

            override fun onResponse(call: Call<MealsNetwork>, response: Response<MealsNetwork>) {
                onSuccess.invoke(response.body()!!.meals)
            }
        })
    }
}
