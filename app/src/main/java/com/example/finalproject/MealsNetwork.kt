package com.example.finalproject

import androidx.room.*

data class MealsNetwork(
    val meals: List<MealNetwork>
)
//{
//    fun toUI(): Meal {
//        return meals[0].toUi()
//    }
//}

@Entity(tableName = LocalDao.TABLE_MEALS)
data class MealNetwork(
    @PrimaryKey(autoGenerate = true) val idMeal: Int,
    val strMeal: String,
    var strCategory: String = "",
    var strArea: String = "",
    var strInstructions: String = "",
    val strMealThumb: String? = null,
    val strTags: String? = null,
    val strYoutube: String? = null,
    val strIngredient1: String? = null,
    val strIngredient2: String? = null,
    val strIngredient3: String? = null,
    val strIngredient4: String? = null,
    val strIngredient5: String? = null,
    val strIngredient6: String? = null,
    val strIngredient7: String? = null,
    val strIngredient8: String? = null,
    val strIngredient9: String? = null,
    val strIngredient10: String? = null,
    val strIngredient11: String? = null,
    val strIngredient12: String? = null,
    val strIngredient13: String? = null,
    val strIngredient14: String? = null,
    val strIngredient15: String? = null,
    val strIngredient16: String? = null,
    val strIngredient17: String? = null,
    val strIngredient18: String? = null,
    val strIngredient19: String? = null,
    val strIngredient20: String? = null,
    val strMeasure1: String? = null,
    val strMeasure2: String? = null,
    val strMeasure3: String? = null,
    val strMeasure4: String? = null,
    val strMeasure5: String? = null,
    val strMeasure6: String? = null,
    val strMeasure7: String? = null,
    val strMeasure8: String? = null,
    val strMeasure9: String? = null,
    val strMeasure10: String? = null,
    val strMeasure11: String? = null,
    val strMeasure12: String? = null,
    val strMeasure13: String? = null,
    val strMeasure14: String? = null,
    val strMeasure15: String? = null,
    val strMeasure16: String? = null,
    val strMeasure17: String? = null,
    val strMeasure18: String? = null,
    val strMeasure19: String? = null,
    val strMeasure20: String? = null,
    var isBookmarked: Boolean? = false
)
//{
//    fun toUi(): Meal {
//        return Meal(id = idMeal,
//            name = strMeal,
//            category = strCategory,
//            area = strArea,
//            instructions = strInstructions,
//            img = strMealThumb,
//            tags = strTags,
//            youtube = strYoutube,
//            ingredients = listOf(
//                listOf(strIngredient1, strMeasure1),
//                listOf(strIngredient2, strMeasure2),
//                listOf(strIngredient3, strMeasure3),
//                listOf(strIngredient4, strMeasure4),
//                listOf(strIngredient5, strMeasure5),
//                listOf(strIngredient6, strMeasure6),
//                listOf(strIngredient7, strMeasure7),
//                listOf(strIngredient8, strMeasure8),
//                listOf(strIngredient9, strMeasure9),
//                listOf(strIngredient10, strMeasure10),
//                listOf(strIngredient11, strMeasure11),
//                listOf(strIngredient12, strMeasure12),
//                listOf(strIngredient13, strMeasure13),
//                listOf(strIngredient14, strMeasure14),
//                listOf(strIngredient15, strMeasure15),
//                listOf(strIngredient16, strMeasure16),
//                listOf(strIngredient17, strMeasure17),
//                listOf(strIngredient18, strMeasure18),
//                listOf(strIngredient19, strMeasure19),
//                listOf(strIngredient20, strMeasure20)
//            )
//        )
//    }
//}

//data class Meal(
//    val id: String,
//    val name: String,
//    val category: String,
//    val area: String,
//    val instructions: String,
//    val img: String,
//    val tags: String,
//    val youtube: String,
//    val ingredients: List<List<String>>
//)

//Завтра займусь тем, что повыше


@Dao
interface LocalDao {
    companion object {
        const val DB_NAME = "meals"
        const val TABLE_INGREDIENT = "ingredient"
        const val TABLE_MEALS = "meals"
    }

    @Query("SELECT * FROM ${LocalDao.TABLE_INGREDIENT}")
    fun getIngredients(): List<Ingredient>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addIngredients(vararg ingr: Ingredient)

    @Query("SELECT * FROM ${LocalDao.TABLE_MEALS}")
    fun getMeals(): List<MealNetwork>

    @Delete
    fun removeMeals(vararg meal: MealNetwork)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMeals(vararg meal: MealNetwork)
}

//@Database(entities = [Ingredient::class, MealNetwork::class], version = 1)
//abstract class MealsDatabase: RoomDatabase() {
//    abstract fun getLocalDao(): LocalDao
//}

data class RemoteResponse<T>(val meals: List<T>)

@Entity(tableName = LocalDao.TABLE_INGREDIENT)
data class Ingredient (
    @PrimaryKey(autoGenerate = true) val idIngredient: Int,
    val strIngredient: String,
    val measure: String?
)


