package com.example.finalproject

import retrofit2.http.Url

data class Meal (
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val mealThum: Url,
    val tags: String,
    val youTube: Url,
    val ingredients: List<Ingradient>
)