package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class BriefInfoAdapter : RecyclerView.Adapter<BriefInfoAdapter.ViewHolder>() {

    private var data: List<MealNetwork>? = null

    fun setMeals(list: List<MealNetwork>){
        this.data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BriefInfoAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).
            inflate(R.layout.item_brief_info, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: BriefInfoAdapter.ViewHolder, pos: Int) {

        holder.name.text = data!![pos].strMeal
        holder.area.text = data!![pos].strArea
        holder.category.text = data!![pos].strCategory
        Picasso.get().load(data!![pos].strMealThumb).into(holder.mealImg)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val area: TextView
        val category: TextView
        val mealImg: ImageView
        init {
            name = view.findViewById(R.id.meal_name)
            area = view.findViewById(R.id.meal_area)
            category = view.findViewById(R.id.meal_category)
            mealImg = view.findViewById(R.id.meal_img)
        }
    }
}
