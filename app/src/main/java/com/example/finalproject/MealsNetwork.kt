package com.example.finalproject

data class MealsNetwork(
    val meals: List<MealNetwork>
)
//{
//    fun toUI(): Meal {
//        return meals[0].toUi()
//    }
//}

data class MealNetwork(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String,
    val strArea: String,
    val strInstructions: String,
    val strMealThumb: String,
    val strTags: String,
    val strYoutube: String,
    val strIngredient1: String,
    val strIngredient2: String,
    val strIngredient3: String,
    val strIngredient4: String,
    val strIngredient5: String,
    val strIngredient6: String,
    val strIngredient7: String,
    val strIngredient8: String,
    val strIngredient9: String,
    val strIngredient10: String,
    val strIngredient11: String,
    val strIngredient12: String,
    val strIngredient13: String,
    val strIngredient14: String,
    val strIngredient15: String,
    val strIngredient16: String,
    val strIngredient17: String,
    val strIngredient18: String,
    val strIngredient19: String,
    val strIngredient20: String,
    val strMeasure1: String,
    val strMeasure2: String,
    val strMeasure3: String,
    val strMeasure4: String,
    val strMeasure5: String,
    val strMeasure6: String,
    val strMeasure7: String,
    val strMeasure8: String,
    val strMeasure9: String,
    val strMeasure10: String,
    val strMeasure11: String,
    val strMeasure12: String,
    val strMeasure13: String,
    val strMeasure14: String,
    val strMeasure15: String,
    val strMeasure16: String,
    val strMeasure17: String,
    val strMeasure18: String,
    val strMeasure19: String,
    val strMeasure20: String
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
