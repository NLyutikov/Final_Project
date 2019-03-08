package com.example.finalproject

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/json/v1/1/randomselection.php")
    fun getRandomMeal(): Call<MealsNetwork>
    @GET("api/json/v1/1/search.php")
    fun getSearchMeal(@Query("s")sch: String): Call<MealsNetwork>
    @GET("api/json/v1/1/filter.php")
    fun getMealByIngredients(@Query("i")ing: String):Call<MealsNetwork>
}