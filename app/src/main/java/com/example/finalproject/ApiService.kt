package com.example.finalproject

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("randomselection.php")
    fun getRandomMeal(): Call<MealsNetwork>

    @GET("search.php")
    fun getSearchMeal(@Query("s") sch: String): Call<MealsNetwork>

    @GET("filter.php")
    fun getMealByIngredients(@Query("i") ing: String): Call<MealsNetwork>

    @GET("list.php?i=list")
    fun getIngredients(): Call<RemoteResponse<Ingredient>>
}
