package com.example.finalproject

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

object ApiManager {

    private val apiServise: ApiService by lazy(LazyThreadSafetyMode.NONE) {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

//    private var db: MealsDatabase? = null

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

    fun getFilteredMeals(
        activity: FragmentActivity?,
        filter: MealFilter,
        onSuccess: ((List<MealNetwork>) -> Unit),
        onFailure: (String) -> Unit
    ) {
        apiServise.getMealByIngredients(filter.ingredients[0].strIngredient)
            .enqueue(object : Callback<MealsNetwork> {
                override fun onFailure(call: Call<MealsNetwork>, t: Throwable) {
                    onFailure.invoke(t.message.toString())
                }

                override fun onResponse(call: Call<MealsNetwork>, response: Response<MealsNetwork>) {
                    if (response.body() == null || response.body()!!.meals.isNullOrEmpty()
                    ) Toast.makeText(
                        activity,
                        "Sorry, there are no dishes with this combination of letters.",
                        Toast.LENGTH_LONG
                    ).show() else onSuccess.invoke(response.body()!!.meals)

                }
            })
    }
}

//Переделай getIngredients по подобию getFilteredMeals, нужно Thread полностью заменить на .enqueue(object:, ну и да,
// тосты тут не понадобятся, т.к., как я понял это фоновая функция

//    fun getIngredients(
//            ctx: Activity,
//            onSuccess: ((List<Ingredient>) -> Unit),
//            onFailure: (String) -> Unit) {
//
//        if(db == null)
//            db = Room.databaseBuilder(ctx, MealsDatabase::class.java, LocalDao.DB_NAME).allowMainThreadQueries().build()
//        Thread {
//            var ingredients: List<Ingredient>?
//            val remoteResponse = apiServise.getIngradients().execute()
//            if(!remoteResponse.isSuccessful || remoteResponse.body() == null || remoteResponse.body()!!.meals.isNullOrEmpty())
//                ingredients =  db!!.getLocalDao().getIngredients()
//            ingredients = remoteResponse.body()!!.meals
//
//            if(ingredients.isNullOrEmpty())
//                ctx.runOnUiThread{ onFailure.invoke("") }
//            else
//                ctx.runOnUiThread{ onSuccess.invoke(ingredients) }
//
//        }.start()
//    }

//С фаворитами таже тема, попробую повзаимодействовать с CheckBox'ами напрямую, думаю, это будет не сложно
//Главная суть, что, как только чекбокс был активирован, на блюде, он должно попасть в БД и висеть там с активным чекбоксом
//Если же чекбокс снимается, то блюдо отваливается от БД

//    fun updateFavorite(ctx: Activity, meal: MealNetwork) {
//        Thread {
//
//            Log.d("current", "updateFavorite " + meal)
//
//            if (db == null)
//                db = Room.databaseBuilder(ctx, MealsDatabase::class.java, LocalDao.DB_NAME).allowMainThreadQueries()
//                    .build()
//
//            if (meal.strCategory == null)
//                meal.strCategory = ""
//            if (meal.strArea == null)
//                meal.strArea = ""
//            if (meal.strInstructions == null)
//                meal.strInstructions = ""
//
//            meal.isBookmarked = if (meal.isBookmarked == null) true else !(meal.isBookmarked as Boolean)
//            if (meal.isBookmarked as Boolean)
//                db!!.getLocalDao().addMeals(meal)
//            else
//                db!!.getLocalDao().removeMeals(meal)
//
//        }.start()
//    }
//

//Убрал остальные параметры, за ненадобностью, ибо всё это происходит отдельно друг от друга
data class MealFilter(
    var ingredients: ArrayList<Ingredient> = ArrayList()
)
