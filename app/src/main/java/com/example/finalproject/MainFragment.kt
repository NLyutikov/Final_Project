package com.example.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.main_layout.view.*

private const val RANDOM_MEALS_SIZE = 10

class MainFragment : Fragment() {

    private lateinit var refreshLayout: SwipeRefreshLayout
    private val adapter = BriefInfoAdapter()
    private val ListOfRandomMeals = APIManager.getRandomMeals(
        quantity = RANDOM_MEALS_SIZE,
        onSuccess = { meals ->
            adapter.setMeals(meals)
        },
        onFailure = { errorMessage ->
            Toast.makeText(activity, "Упси, ошибочка: ${errorMessage}", Toast.LENGTH_LONG).show()
        }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_layout, container, false)

        val mealList = view.main_recycler
        mealList.adapter = adapter
        refreshLayout = view.refresh

        ListOfRandomMeals
        refreshLayout.isRefreshing = true

        refreshLayout.setOnRefreshListener {
            ListOfRandomMeals
        }

        return view
    }
}
