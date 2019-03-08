package com.example.finalproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import kotlinx.android.synthetic.main.main_layout.view.*
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class MainFragment : Fragment() {
    private lateinit var refreshLayout: SwipeRefreshLayout
    private var adapter = BriefInfoAdapter()

    @SuppressLint("CheckResult", "ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_layout, container, false)

        //Кнопку пока не трогать, я пока толком нормальне не настроил логику onClick, в MainActivity и из-за неё
        // приложуха крашится, так что, перехода на страницу фильтрации пока нету
//        view.toolbar_filter.setOnClickListener {
//            (view.toolbar_filter.context as ClickCallback).onClick(view.toolbar_filter)
//            Log.d("chck", toolbar_filter.context.toString())
//        }

        val mealList = view.findViewById<RecyclerView>(R.id.main_recycler)
        mealList.adapter = adapter
        mealList.layoutManager = LinearLayoutManager(activity)
        refreshLayout = view.refresh

        getRandomMealsAndShowThem()
        refreshLayout.isRefreshing = true

        refreshLayout.setOnRefreshListener {
            getRandomMealsAndShowThem()
        }

        Observable.create(ObservableOnSubscribe<String> { subscriber ->
            view.toolbar_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    subscriber.onNext(query!!)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    subscriber.onNext(newText!!)
                    return false
                }
            })
        })
            .map { text -> text.toLowerCase().trim() }
            .debounce(250, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .filter { text -> text.isNotBlank() }
            .filter { text -> text.length > 1 }
            .subscribe { text ->
                ApiManager.getSearchMeals(
                    activity = activity,
                    text = text,
                    onSuccess = { meals ->
                        adapter.setMeals(meals)
                    },
                    onFailure = { errorMassege ->
                        Snackbar.make(view, errorMassege, Snackbar.LENGTH_SHORT)
                    }
                )
            }

        return view
    }

    private fun getRandomMealsAndShowThem() {
        ApiManager.getRandomMeals(
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
