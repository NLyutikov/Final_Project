package com.example.finalproject

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val retrofit = Retrofit.Builder()
    .baseUrl("https://www.themealdb.com/api/json/v1/1/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

var ingredients: List<Ingredient>? = null
val selectedIngredients: ArrayList<Ingredient> = ArrayList<Ingredient>()


class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val ingredientAdapter = IngredientAdapter(this)


        retrofit.create(ApiService::class.java).getIngradients().enqueue(object: Callback<RemoteResponse<Ingredient>> {
            override fun onFailure(call: Call<RemoteResponse<Ingredient>>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(
                call: Call<RemoteResponse<Ingredient>>,
                response: Response<RemoteResponse<Ingredient>>
            ) {
                ingredients = response.body()?.meals
                ingredientAdapter.dataSet = ingredients?.slice(0..10)
                findViewById<RecyclerView>(R.id.vIngredients).apply {
                    adapter = ingredientAdapter
                    layoutManager = LinearLayoutManager(this@Main2Activity)
                }
            }
        })

        goFilter.setOnClickListener {
            if(selectedIngredients.isEmpty()) {
                Snackbar.make(goFilter, "please, select some ingredients", Snackbar.LENGTH_LONG).show()
            } else {

            }

        }
    }
}

class MyHolder(val view: View): RecyclerView.ViewHolder(view)

class IngredientAdapter(val ctx: Activity): RecyclerView.Adapter<MyHolder>() {

    var dataSet: List<Ingredient>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder((ctx as Activity).layoutInflater.inflate(R.layout.ingredient_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return dataSet?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        if(dataSet == null)
            throw Exception("ingredientAdapter dataSet is null")

        val item: Ingredient = dataSet?.get(position) ?: throw Exception("ingredientAdapter dataSet item is null")

        holder.view.apply {
            findViewById<TextView>(R.id.ingredientTitle)?.text = item.strIngredient
            val chips = (ctx as MainActivity).findViewById<ChipGroup>(R.id.chipGroup)

            setOnClickListener {

                createChips(ctx as MainActivity, item)
            }
        }
    }
}


fun createChips(ctx: MainActivity, item: Ingredient, viewWithChipsGroup: ChipGroup = ctx.findViewById<ChipGroup>(R.id.chipGroup)) {

    if(selectedIngredients.contains(item))
        return

    val newChip = Chip(ctx)
    newChip.text = item.strIngredient
    newChip.isClickable = true
    newChip.isCheckable = false
    newChip.setChipDrawable(ChipDrawable.createFromResource(ctx, R.layout.chip_item))
    selectedIngredients.add(item)

    newChip.setOnCloseIconClickListener {
        viewWithChipsGroup.removeView(newChip)
        selectedIngredients.remove(item)
    }
    viewWithChipsGroup.addView(newChip)
}




