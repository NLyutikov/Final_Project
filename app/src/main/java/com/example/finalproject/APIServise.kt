package com.example.finalproject

import retrofit2.Call
import retrofit2.http.GET

interface APIServise {
    @GET("api/json/v1/1/random.php")
    fun getRandomMeal(): Call<List<Meal>>
}