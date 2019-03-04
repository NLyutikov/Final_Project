package com.example.finalproject

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.main_layout.view.*

const val RANDOM_MEALS_SIZE = 10


const val LIST_TYPE = "LIST_TYPE"
const val LIST_TYPE_RANDOM= 0
const val LIST_TYPE_BOOKMARK = 1


abstract class ListFragment: Fragment() {

    protected lateinit var refreshLayout: SwipeRefreshLayout
    protected lateinit var adapter: BriefInfoAdapter
    protected var listType: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(arguments != null)
            listType = (arguments as Bundle).getInt(LIST_TYPE, LIST_TYPE_RANDOM)
        adapter = BriefInfoAdapter(activity!! as MainActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_layout, container, false)

        val mealList = view.findViewById<RecyclerView>(R.id.main_recycler)
        mealList.adapter = adapter
        mealList.layoutManager = LinearLayoutManager(activity)
        refreshLayout = view.refresh


        loadData()
        refreshLayout.isRefreshing = true

        // only random list load another data, else refresh useless
        if(listType == LIST_TYPE_RANDOM) {
            refreshLayout.setOnRefreshListener {
                loadData()
            }
        }

        return view
    }
    abstract fun loadData()
}


class MainFragment : ListFragment() {

    override fun loadData(){
        ApiManager.getRandomMeals(
            activity = activity,
            quantity = RANDOM_MEALS_SIZE,
            onSuccess = { meals ->
                adapter.setMeals(meals)
                refreshLayout.isRefreshing = false
            },
            onFailure = { errorMessage ->
                Toast.makeText(activity, "Oops, error: $errorMessage", Toast.LENGTH_LONG).show()
                refreshLayout.isRefreshing = false
            }
        )
    }
}


class FiltredListFragment: ListFragment() {

    val filter: MealFilter = MealFilter()

    override fun loadData() {
        ApiManager.getMeals(
            activity = activity,
            filter = filter,
            onSuccess = { meals ->
                adapter.setMeals(meals)
                refreshLayout.isRefreshing = false

            },
            onFailure = { errorMessage ->
                Toast.makeText(activity, "Oops, error: $errorMessage", Toast.LENGTH_LONG).show()
                refreshLayout.isRefreshing = false
            }
        )
    }
}



class Filter: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_main2, container, false)
        if(activity == null)
            return view

        selectedIngredients.clear()
        //for(chips in selectedIngredients)
        //    createChips(activity as MainActivity, chips, view.findViewById(R.id.chipGroup))

        val ingredientAdapter = IngredientAdapter(activity as MainActivity)

        view.findViewById<EditText>(R.id.chipsInput).addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s == null)
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
            {loadedIngredients ->
                ingredients = loadedIngredients
                ingredientAdapter.dataSet = ingredients?.slice(0..10)
                view.findViewById<RecyclerView>(R.id.vIngredients).apply {
                    adapter = ingredientAdapter
                    layoutManager = LinearLayoutManager(activity)
                }
            },
            {errorMessage -> Toast.makeText(activity, "Oops, error: $errorMessage", Toast.LENGTH_LONG).show()})

        view.findViewById<Button>(R.id.goFilter).setOnClickListener {
            if(selectedIngredients.isEmpty()) {
                Snackbar.make(goFilter, "please, select some ingredients", Snackbar.LENGTH_LONG).show()
            } else {
                (activity as MainActivity).toFragment(FRAGMENT_FILTRED_LIST, fun(newFragment) {
                    (newFragment as FiltredListFragment).filter.ingredients = selectedIngredients
                })
            }

        }
        return view
    }
}


class DetailFragment: Fragment() {

    var meal: MealNetwork? = null
        set(value) {if(value != null) field = value}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.meal_detail, container, false)
        view.findViewById<TextView>(R.id.vMealTitle).text = meal?.strMeal
        return view
    }
}
