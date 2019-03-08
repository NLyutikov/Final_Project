package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class BriefInfoAdapter(private val ctx: MainActivity) : RecyclerView.Adapter<BriefInfoAdapter.ViewHolder>() {

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
        holder.itemView.setOnClickListener {
            ctx.toFragment(FRAGMENT_DETAIL, {
                (it as DetailFragment).meal = data!![pos]
            })
        }
      //Не забудь, кнопки фаворитов теперь чекбоксы и я не уверен, что это вообще здесь нужно(Никита) 
        holder.btnFavorite.setOnClickListener {
            ApiManager.updateFavorite(ctx, data!![pos])
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
