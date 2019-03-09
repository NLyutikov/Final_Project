package com.example.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso


class DetailFragment : FragmentWithToolbar() {

    var meal: MealNetwork? = null
        set(value) {if(value != null) field = value}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.meal_detail, container, false)

        view.findViewById<TextView>(R.id.v_meal_title).text = meal?.strMeal
        Picasso.get().load(meal!!.strMealThumb).into(view.findViewById<ImageView>(R.id.v_meal_image))
        view.findViewById<TextView>(R.id.v_meal_description).text = meal?.strInstructions

        return view
    }
}