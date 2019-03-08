package com.example.finalproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
  
const val LIST_TYPE = "LIST_TYPE"
const val LIST_TYPE_RANDOM= 0

abstract class ListFragment: Fragment() {

    protected lateinit var refreshLayout: SwipeRefreshLayout
    protected lateinit var adapter: BriefInfoAdapter
    private var listType: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(arguments != null)
            listType = (arguments as Bundle).getInt(LIST_TYPE, LIST_TYPE_RANDOM)
        adapter = BriefInfoAdapter(activity!! as MainActivity)
    }

    @SuppressLint("CheckResult", "ResourceAsColor")
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
                    text = text,
                    onSuccess = { meals ->
                        adapter.setMeals(meals)
                    },
                    onFailure = { errorMessage ->
                        Snackbar.make(View(activity), errorMessage, Snackbar.LENGTH_SHORT)
                    }
                )
            }

        return view
    }
    abstract fun loadData()
}

class MainFragment : ListFragment() {

    override fun loadData(){
        ApiManager.getRandomMeals(
            onSuccess = { meals ->
                adapter.setMeals(meals)
                refreshLayout.isRefreshing = false
            },
            onFailure = { errorMessage ->
                Toast.makeText(activity, resources.getString(R.string.err_with_descr, errorMessage), Toast.LENGTH_LONG)
                    .show()
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
                Toast.makeText(activity, resources.getString(R.string.err_with_descr, errorMessage), Toast.LENGTH_LONG)
                    .show()
                refreshLayout.isRefreshing = false
            }
        )
    }
}

class FavoriteListFragment : ListFragment() {

    val filter: MealFilter = MealFilter(favorite = true)

    override fun loadData() {
        ApiManager.getMeals(
            activity = activity,
            filter = filter,
            onSuccess = { meals ->
                adapter.setMeals(meals)
                refreshLayout.isRefreshing = false

            },
            onFailure = { errorMessage ->
                Toast.makeText(activity, resources.getString(R.string.err_with_descr, errorMessage), Toast.LENGTH_LONG)
                    .show()
                refreshLayout.isRefreshing = false
            }
        )
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
