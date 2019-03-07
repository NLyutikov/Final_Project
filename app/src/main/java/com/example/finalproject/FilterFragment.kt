package com.example.finalproject

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Filter : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_main2, container, false)
        if (activity == null)
            return view

        selectedIngredients.clear()
        //for(chips in selectedIngredients)
        //    createChips(activity as MainActivity, chips, view.findViewById(R.id.chipGroup))

        val ingredientAdapter = IngredientAdapter(activity as MainActivity)

        view.findViewById<EditText>(R.id.chipsInput).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s == null)
                    return
                ingredientAdapter.dataSet = ingredients?.filter { it.strIngredient.startsWith(s.toString(), true) }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
/*
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
                view.findViewById<RecyclerView>(R.id.vIngredients).apply {
                    adapter = ingredientAdapter
                    layoutManager = LinearLayoutManager(activity)
                }
            }
        })
        */

        ApiManager.getIngredients(
            activity!!,
            { loadedIngredients ->
                ingredients = loadedIngredients
                ingredientAdapter.dataSet = ingredients?.slice(0..10)
                view.findViewById<RecyclerView>(R.id.vIngredients).apply {
                    adapter = ingredientAdapter
                    layoutManager = LinearLayoutManager(activity)
                }
            },
            { errorMessage -> Toast.makeText(activity, "Oops, error: $errorMessage", Toast.LENGTH_LONG).show() })

        view.findViewById<Button>(R.id.goFilter).setOnClickListener {
            if (selectedIngredients.isEmpty()) {
                Snackbar.make(view.findViewById(R.id.goFilter), "please, select some ingredients", Snackbar.LENGTH_LONG)
                    .show()
            } else {
                (activity as MainActivity).toFragment(FRAGMENT_FILTRED_LIST, fun(newFragment) {
                    (newFragment as FiltredListFragment).filter.ingredients = selectedIngredients
                })
            }

        }
        return view
    }
}


val retrofit = Retrofit.Builder()
    .baseUrl("https://www.themealdb.com/api/json/v1/1/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

var ingredients: List<Ingredient>? = null
val selectedIngredients: ArrayList<Ingredient> = ArrayList<Ingredient>()


class MyHolder(val view: View) : RecyclerView.ViewHolder(view)

class IngredientAdapter(val ctx: Activity) : RecyclerView.Adapter<MyHolder>() {

    var dataSet: List<Ingredient>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ctx.layoutInflater.inflate(R.layout.ingredient_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return dataSet?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        if (dataSet == null)
            throw Exception("ingredientAdapter dataSet is null")

        val item: Ingredient = dataSet?.get(position) ?: throw Exception("ingredientAdapter dataSet item is null")

        holder.view.apply {
            findViewById<TextView>(R.id.ingredientTitle)?.text = item.strIngredient
            val chips = (ctx as MainActivity).findViewById<ChipGroup>(R.id.chipGroup)

            setOnClickListener {

                createChips(ctx, item)
            }
        }
    }
}


fun createChips(
    ctx: MainActivity,
    item: Ingredient,
    viewWithChipsGroup: ChipGroup = ctx.findViewById<ChipGroup>(R.id.chipGroup)
) {

    if (selectedIngredients.contains(item))
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