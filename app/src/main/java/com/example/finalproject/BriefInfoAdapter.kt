package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.*

internal class BriefInfoAdapter: RecyclerView.Adapter<BriefInfoAdapter.ViewHolder>() {

    var data: LinkedList<Meal>? = null

    fun setMeals(list: LinkedList<Meal>){
        this.data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BriefInfoAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_brief_info, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: BriefInfoAdapter.ViewHolder, pos: Int) {
        holder.name.text = data!![pos].name
        holder.area.text = data!![pos].area
        holder.category.text = data!![pos].category
        Picasso.get().load(data!![pos].mealImg).into(holder.mealImg)
    }

    internal class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val area: TextView
        val category: TextView
        val mealImg: ImageView
        init {
            name = view.findViewById(R.id.title)
            area = view.findViewById(R.id.meal_area)
            category = view.findViewById(R.id.meal_category)
            mealImg = view.findViewById(R.id.imageView)
        }
    }
}