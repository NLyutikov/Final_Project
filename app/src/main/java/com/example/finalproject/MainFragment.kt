package com.example.finalproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.main_layout.view.*

const val RANDOM_MEALS_SIZE = 10


const val LIST_TYPE = "LIST_TYPE"
const val LIST_TYPE_RANDOM= 0


abstract class FragmentWithToolbar : Fragment() {

    protected fun setToolbarActions(view: View) {
        view.findViewById<ImageButton>(R.id.toolbar_filter)?.setOnClickListener {
            (activity as MainActivity).toFragment(FRAGMENT_FILTER)
        }
        view.findViewById<CheckBox>(R.id.toolbar_favorites)?.setOnClickListener {
            (activity as MainActivity).toFragment(FRAGMENT_FAVORITE)
        }
    }
}

abstract class ListFragment : FragmentWithToolbar() {

    protected lateinit var refreshLayout: SwipeRefreshLayout
    protected lateinit var adapter: BriefInfoAdapter
    private var listType: Int = 0


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

    @SuppressLint("CheckResult", "ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_layout, container, false)

        //Кнопку пока не трогать, я пока толком нормальне не настроил логику onClick, в MainActivity и из-за неё
        // приложуха крашится, так что, перехода на страницу фильтрации пока нету

        // see setToolbarActions

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

        /*
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
*/
        setToolbarActions(view)
        return view
    }

    override fun loadData(){
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


class DetailFragment : FragmentWithToolbar() {

    var meal: MealNetwork? = null
        set(value) {if(value != null) field = value}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.meal_detail, container, false)
        view.findViewById<TextView>(R.id.v_meal_title).text = meal?.strMeal
        return view
    }
}
