package com.example.finalproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.main_layout.view.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainFragment: Fragment() {

    private val list: LinkedList<Meal> = LinkedList()
    private lateinit var refreshLayout: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_layout, container, false)

        val mealList = view.findViewById<RecyclerView>(R.id.main_recycler)
        refreshLayout = view.refresh
        if (list.isEmpty()) {
            request()
            refreshLayout.isRefreshing = true
        }
        refreshLayout.setOnRefreshListener {
            request()
        }

        return view
    }

    private fun request()  {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiServise = retrofit.create(APIServise::class.java)
        Thread{
            val list = LinkedList<Meal>()
            //loader.start()
            for (i in 0 until 10) {
                val response = apiServise.getRandomMeal().execute()
                if (response.isSuccessful) {
                    //Обработка успеха
                } else {
                    //Обработка ошибок
                }
            }
            //loader.stop()
            // adapter.add(list)
            Log.d("THREAD", list.toString())
        }.start()

    }
}
