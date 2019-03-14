package com.example.finalproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_dish.view.*

class DetailFragment : Fragment() {
    companion object {
        const val tag = "detail_fragment"
        const val MEAL_KEY = "full_info_meal"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.detailed_dish_info, container, false)

        val thisMeal = (arguments?.getSerializable(MEAL_KEY) as? MealNetwork)
        val toUI = thisMeal?.toUi()!!

        view.dish_title.text = toUI.name
        view.full_info_favorites.isChecked = toUI.like
        Picasso.get().load(toUI.img).into(view.dish_img)
        view.dish_inst.text = toUI.instructions
        for (num in 0 until 19) {
            if (toUI.ingredients[num].ingredient.toString() != "" && toUI.ingredients[num].ingredient != null) {
                view.ingredients.text = view.ingredients.text.toString() +
                        "${toUI.ingredients[num].ingredient} - ${toUI.ingredients[num].measure}\n"
            }
        }
        view.full_info_favorites.setOnCheckedChangeListener { _, isChecked ->
            toUI.like = isChecked
            ApiManager.updateFavorite(view.context, thisMeal, isChecked)
        }
        return view
    }
}
