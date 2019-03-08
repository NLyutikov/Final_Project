package com.example.finalproject

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.Activity
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

object ApiManager {

    private val apiServise: ApiService by lazy(LazyThreadSafetyMode.NONE) {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private var db: MealsDatabase? = null

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

    fun getMeals(
        activity: FragmentActivity?,
        filter: MealFilter,
        onSuccess: ((List<MealNetwork>) -> Unit),
        onFailure: (String) -> Unit
    ) {
        if (filter.ingredients.isEmpty() && filter.searchWord.isEmpty() && !filter.favorite) {
            activity?.runOnUiThread {
                onFailure.invoke(
                    activity?.resources?.getString(R.string.err_empty_filter) ?: ""
                )
            }
            return
        }

        Thread {
            if (filter.favorite) {
                if (db == null)
                    db = Room.databaseBuilder(activity!!, MealsDatabase::class.java, LocalDao.DB_NAME)
                        .allowMainThreadQueries().build()
                val localResponse = db!!.getLocalDao().getMeals()
                activity?.runOnUiThread { onSuccess.invoke(localResponse) }
                Log.d("current", "DB getMeals " + localResponse)
            } else {
                val response =
                    when {
                        //filter.searchWord.isEmpty() -> apiServise.getMealByIngredients(filter.ingredients.joinToString(separator = " ") { it.strIngredient}).execute()
                        filter.searchWord.isEmpty() -> apiServise.getMealByIngredients(filter.ingredients[0].strIngredient).execute()
                        else -> apiServise.getSearchMeal(filter.searchWord).execute()
                    }
                if (response.isSuccessful) {
                    activity?.runOnUiThread {
                        onSuccess.invoke(if (response.body() == null || response.body()!!.meals.isNullOrEmpty()) ArrayList() else response.body()!!.meals)
                    }
                } else {
                    activity?.runOnUiThread { onFailure.invoke(response.errorBody()!!.string()) }
                }
            }
        }.start()
    }

    fun getIngredients(
            ctx: Activity,
            onSuccess: ((List<Ingredient>) -> Unit),
            onFailure: (String) -> Unit) {

        if(db == null)
            db = Room.databaseBuilder(ctx, MealsDatabase::class.java, LocalDao.DB_NAME).allowMainThreadQueries().build()
        Thread {
            var ingredients: List<Ingredient>?
            val remoteResponse = apiServise.getIngradients().execute()
            if(!remoteResponse.isSuccessful || remoteResponse.body() == null || remoteResponse.body()!!.meals.isNullOrEmpty())
                ingredients =  db!!.getLocalDao().getIngredients()
            ingredients = remoteResponse.body()!!.meals

            if(ingredients.isNullOrEmpty())
                ctx.runOnUiThread{ onFailure.invoke("") }
            else
                ctx.runOnUiThread{ onSuccess.invoke(ingredients) }

        }.start()
    }

    fun updateFavorite(ctx: Activity, meal: MealNetwork) {
        Thread {

            Log.d("current", "updateFavorite " + meal)

            if (db == null)
                db = Room.databaseBuilder(ctx, MealsDatabase::class.java, LocalDao.DB_NAME).allowMainThreadQueries()
                    .build()

            if (meal.strCategory == null)
                meal.strCategory = ""
            if (meal.strArea == null)
                meal.strArea = ""
            if (meal.strInstructions == null)
                meal.strInstructions = ""

            meal.isBookmarked = if (meal.isBookmarked == null) true else !(meal.isBookmarked as Boolean)
            if (meal.isBookmarked as Boolean)
                db!!.getLocalDao().addMeals(meal)
            else
                db!!.getLocalDao().removeMeals(meal)

        }.start()
    }
}


data class MealFilter(
    var ingredients: ArrayList<Ingredient> = ArrayList(),
    var searchWord: String = "",
    var favorite: Boolean = false
)
