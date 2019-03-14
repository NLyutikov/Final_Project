@file:Suppress("DEPRECATION")

package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class BriefInfoAdapter : RecyclerView.Adapter<BriefInfoAdapter.ViewHolder>() {

    private var data: List<MealNetwork>? = null

    fun setMeals(list: List<MealNetwork>) {
        this.data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_brief_info, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {

        holder.name.text = data!![pos].strMeal
        holder.area.text = data!![pos].strArea
        holder.category.text = data!![pos].strCategory
        Picasso.get().load(data!![pos].strMealThumb).into(holder.mealImg)
        holder.btnFavorite.isChecked = data!![pos].isBookmarked
        holder.itemView.setOnClickListener {
            (holder.view.context as ClickCallback).onClick(data!![pos])
        }
        holder.btnFavorite.setOnCheckedChangeListener { _, isChecked ->
            ApiManager.updateFavorite(holder.view.context, data!![pos], isChecked)
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val area: TextView
        val category: TextView
        val mealImg: ImageView
        val btnFavorite: CheckBox

        init {
            name = view.findViewById(R.id.meal_name)
            area = view.findViewById(R.id.meal_area)
            category = view.findViewById(R.id.meal_category)
            mealImg = view.findViewById(R.id.meal_img)
            btnFavorite = view.findViewById(R.id.favorites_button)
        }
    }
}
