package com.example.finalproject

import android.util.Log
import androidx.fragment.app.FragmentActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.*

private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

object ApiManager {

    private val apiServise: ApiService by lazy(LazyThreadSafetyMode.NONE) {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun getRandomMeals(
        activity: FragmentActivity?,
        quantity: Int,
        onSuccess: ((LinkedList<MealNetwork>) -> Unit),
        onFailure: (String) -> Unit
    ) {
        Thread {
            val list = LinkedList<MealNetwork>()
            for (i in 0 until quantity) {
                val response = apiServise.getRandomMeal().execute()
                if (response.isSuccessful) {
                    list.add(response.body()!!.meals[0])
                } else {
                    activity?.runOnUiThread { onFailure.invoke(response.errorBody()!!.string()) }
                    break
                }
            }
            activity?.runOnUiThread { onSuccess.invoke(list) }
        }.start()
    }

    fun getMeals(
        activity: FragmentActivity?,
        filter: MealFilter,
        onSuccess: ((LinkedList<MealNetwork>) -> Unit),
        onFailure: (String) -> Unit
    ) {
        if(filter.ingredients.isEmpty() && filter.searchWord.isEmpty())
            throw Exception("filter cannot be empty")

        Thread {
            val response = if(filter.searchWord.isEmpty())
                    apiServise.getMealByIngredients(filter.ingredients[0].strIngredient).execute()
                else
                    apiServise.getSearchMeal(filter.searchWord).execute()

            Log.d("current", "response = " + response.body())
            if (response.isSuccessful && !response.body()!!.meals.isNullOrEmpty()) {
                activity?.runOnUiThread { onSuccess.invoke(LinkedList(response.body()!!.meals)) }
            } else {
                activity?.runOnUiThread { onFailure.invoke(response.errorBody()!!.string()) }
            }

        }.start()
    }
}


data class MealFilter(var ingredients: ArrayList<Ingredient> = ArrayList(), var searchWord: String = "")
