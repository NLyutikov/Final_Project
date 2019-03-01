package com.example.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.main_layout.view.*
import java.lang.IllegalStateException

private const val RANDOM_MEALS_SIZE = 10

class MainFragment : Fragment() {

    private lateinit var refreshLayout: SwipeRefreshLayout
    private val adapter = BriefInfoAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_layout, container, false)

        val mealList = view.findViewById<RecyclerView>(R.id.main_recycler)
        mealList.adapter = adapter
        mealList.layoutManager = LinearLayoutManager(activity)
        refreshLayout = view.refresh


        getRandomMealsAndShowThem()
        refreshLayout.isRefreshing = true


        refreshLayout.setOnRefreshListener {
            getRandomMealsAndShowThem()
        }

        return view
    }
    private fun getRandomMealsAndShowThem(){
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
