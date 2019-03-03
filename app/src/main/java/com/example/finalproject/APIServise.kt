package com.example.finalproject

import retrofit2.Call
import retrofit2.http.GET

interface APIServise {
    @GET("api/json/v1/1/random.php")
    fun getRandomMeal(): Call<List<Meal>>

    @GET("list.php?i=list")
    fun getIngradients(): Call<RemoteResponse<Ingredient>>
}


data class RemoteResponse<T>(val meals: List<T>)