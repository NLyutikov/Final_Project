package com.example.finalproject

data class Meal (
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val mealThum: String,
    val tags: String,
    val youTube: String,
    val ingredients: List<Ingradient>
)
