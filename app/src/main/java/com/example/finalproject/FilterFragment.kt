@file:Suppress("DEPRECATION")

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

class FilterFragment : Fragment() {

    companion object {
        const val tag = "filter_fragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.filter_layout, container, false)
        if (activity == null)
            return view

        selectedIngredients.clear()

        val ingredientAdapter = IngredientAdapter(activity as MainActivity)

        view.findViewById<EditText>(R.id.chips_input).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s == null)
                    return
                ingredientAdapter.dataSet = ingredients?.filter { it.strIngredient.startsWith(s.toString(), true) }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        ApiManager.getIngredients(
            activity!!,
            { loadedIngredients ->
                ingredients = loadedIngredients
                ingredientAdapter.dataSet = ingredients?.slice(0..10)
                view.findViewById<RecyclerView>(R.id.v_ingredients).apply {
                    adapter = ingredientAdapter
                    layoutManager = LinearLayoutManager(activity)
                }
            },
            { errorMessage ->
                Toast.makeText(
                    activity,
                    "${getText(R.string.err_with_descr)}$errorMessage",
                    Toast.LENGTH_LONG
                ).show()
            })

        view.findViewById<Button>(R.id.go_filter).setOnClickListener {
            if (selectedIngredients.isEmpty()) {
                Toast.makeText(
                    activity,
                    getText(R.string.err_empty_filter),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                (activity as ClickCallback).toFragment(MainFragment.tag)
            }

        }
        return view
    }
}

var ingredients: List<Ingredient>? = null
val selectedIngredients: ArrayList<Ingredient> = ArrayList()


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
            findViewById<TextView>(R.id.ingredient_title)?.text = item.strIngredient
            (ctx as MainActivity).findViewById<ChipGroup>(R.id.chip_group)

            setOnClickListener {
                createChips(ctx, item)
            }
        }
    }
}

fun createChips(
    ctx: MainActivity,
    item: Ingredient,
    viewWithChipsGroup: ChipGroup = ctx.findViewById(R.id.chip_group)
) {

    if (selectedIngredients.contains(item))
        return

    val newChip = Chip(ctx)
    newChip.text = item.strIngredient
    newChip.isClickable = false
    newChip.isCheckable = false
    newChip.setChipDrawable(ChipDrawable.createFromResource(ctx, R.xml.ship_item))
    selectedIngredients.add(item)

    newChip.setOnCloseIconClickListener {
        viewWithChipsGroup.removeView(newChip)
        selectedIngredients.remove(item)
    }
    viewWithChipsGroup.addView(newChip)
}
