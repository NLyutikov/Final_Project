package com.example.finalproject

import android.app.Activity
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    private var db: MealsDatabase? = null

    fun getRandomMeals(
        activity: FragmentActivity?,
        quantity: Int,
        onSuccess: ((LinkedList<MealNetwork>) -> Unit),
        onFailure: (String) -> Unit
    ) {
        Thread {
            Log.d("current", "getRandomMeals")
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
                    db = Room.databaseBuilder(activity!!, MealsDatabase::class.java, LocalDao.DB_NAME).build()
                db!!.getLocalDao().getMeals()
            } else {

                val response =
                    when {
                        filter.searchWord.isEmpty() -> apiServise.getMealByIngredients(filter.ingredients.joinToString { it.strIngredient + " " }).execute()
                        else -> apiServise.getSearchMeal(filter.searchWord).execute()
                    }

                if (response.isSuccessful && !response.body()!!.meals.isNullOrEmpty()) {
                    activity?.runOnUiThread { onSuccess.invoke(LinkedList(response.body()!!.meals)) }
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
            db = Room.databaseBuilder(ctx, MealsDatabase::class.java, LocalDao.DB_NAME).build()
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

            if (db == null)
                db = Room.databaseBuilder(ctx, MealsDatabase::class.java, LocalDao.DB_NAME).build()

            meal.isBookmarked = if (meal.isBookmarked == null) true else !(meal.isBookmarked as Boolean)
            db!!.getLocalDao().addMeals(meal)

        }.start()
    }
}


data class MealFilter(
    var ingredients: ArrayList<Ingredient> = ArrayList(),
    var searchWord: String = "",
    var favorite: Boolean = false
)
