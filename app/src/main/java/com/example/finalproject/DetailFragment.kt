package com.example.finalproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_dish.view.*


class DetailFragment : FragmentWithToolbar() {

    var meal: MealNetwork? = null
        set(value) {
            if (value != null) field = value
        }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.detailed_dish_info, container, false)

        view.dish_title.text = meal?.toUi()?.name
        Picasso.get().load(meal?.toUi()?.img).into(view.dish_img)
        view.dish_inst.text = meal?.toUi()?.instructions
        for (num in 0 until 19) {
            if (meal?.toUi()!!.ingredients[num].ingredient.toString() != "" && meal?.toUi()!!.ingredients[num].ingredient != null) {
                view.ingredients.text = view.ingredients.text.toString() +
                        "${meal?.toUi()!!.ingredients[num].ingredient} - ${meal?.toUi()!!.ingredients[num].measure}\n"
            }
        }

        return view
    }
}
