package com.example.finalproject

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        onSuccess: ((List<MealNetwork>) -> Unit),
        onFailure: (String) -> Unit
    ) {
        apiServise.getRandomMeal().enqueue(object : Callback<MealsNetwork> {
            override fun onFailure(call: Call<MealsNetwork>, t: Throwable) {
                onFailure.invoke(t.toString())
            }

            override fun onResponse(call: Call<MealsNetwork>, response: Response<MealsNetwork>) {
                Log.d("Check", response.body()!!.meals.size.toString())
                onSuccess.invoke(response.body()!!.meals)
            }
        })
    }

    fun getSearchMeals(
        activity: FragmentActivity?,
        text: String,
        onSuccess: (List<MealNetwork>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        apiServise.getSearchMeal(text).enqueue(object : Callback<MealsNetwork> {
            override fun onFailure(call: Call<MealsNetwork>, t: Throwable) {
                onFailure.invoke(t.toString())
            }

            override fun onResponse(call: Call<MealsNetwork>, response: Response<MealsNetwork>) {
                if (response.body()?.meals == null) {
                    Toast.makeText(
                        activity,
                        "Sorry, there are no dishes with this combination of letters.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    onSuccess.invoke(response.body()!!.meals)
                }
            }
        })
    }

    fun getMealsByIngredients(
        activity: FragmentActivity?,
        ingredient: String,
        onSuccess: (List<MealNetwork>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        apiServise.getMealByIngredients(ingredient).enqueue(object : Callback<MealsNetwork> {
            override fun onFailure(call: Call<MealsNetwork>, t: Throwable) {
                onFailure.invoke(t.toString())
            }

            override fun onResponse(call: Call<MealsNetwork>, response: Response<MealsNetwork>) {
                val list = LinkedList<MealNetwork>()
                for (i in 0 until response.body()!!.meals.size) {
                    apiServise.getSearchMeal(response.body()!!.meals[i].strMeal)
                        .enqueue(object : Callback<MealsNetwork> {
                            override fun onFailure(call: Call<MealsNetwork>, t: Throwable) {
                            }

                            override fun onResponse(call: Call<MealsNetwork>, response: Response<MealsNetwork>) {
                                list.add(response.body()?.meals!![0])
                            }
                        })
                }
                onSuccess.invoke(list)
            }
        })
    }

    fun getFavoriteMeals(
        activity: FragmentActivity?,
        onSuccess: (List<MealNetwork>) -> Unit
    ) {
        if (db == null)
            db = Room.databaseBuilder(activity!!, MealsDatabase::class.java, LocalDao.DB_NAME)
                .allowMainThreadQueries().build()

        val localResponse = db!!.getLocalDao().getMeals()
        activity?.runOnUiThread { onSuccess.invoke(localResponse) }
    }

    fun getIngredients(
        ctx: Activity,
        onSuccess: ((List<Ingredient>) -> Unit),
        onFailure: (String) -> Unit
    ) {

        if (db == null)
            db = Room.databaseBuilder(ctx, MealsDatabase::class.java, LocalDao.DB_NAME).allowMainThreadQueries().build()
        Thread {
            var ingredients: List<Ingredient>?
            try {
                val remoteResponse = apiServise.getIngredients().execute()
                //if (!remoteResponse.isSuccessful || remoteResponse.body() == null || remoteResponse.body()!!.meals.isNullOrEmpty())

                ingredients = remoteResponse.body()!!.meals

            } catch (e: Exception) {

                ingredients = db!!.getLocalDao().getIngredients()
            }

            if (ingredients.isNullOrEmpty())
                ctx.runOnUiThread { onFailure.invoke("") }
            else {
                ctx.runOnUiThread { onSuccess.invoke(ingredients) }
                db!!.getLocalDao().addIngredients(*ingredients.toTypedArray())
            }
        }.start()
    }

    fun updateFavorite(ctx: Context, meal: MealNetwork, checkBox: Boolean) {
        Thread {
            if (db == null)
                db = Room.databaseBuilder(ctx, MealsDatabase::class.java, LocalDao.DB_NAME).allowMainThreadQueries()
                    .build()

            if (checkBox) {
                meal.isBookmarked = checkBox
                db!!.getLocalDao().addMeals(meal)
            } else {
                db!!.getLocalDao().removeMeals(meal)
                meal.isBookmarked = checkBox
            }
        }.start()
    }
}
