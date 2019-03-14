package com.example.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.main_layout.view.*
import kotlinx.android.synthetic.main.toolbar.view.*
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class MainFragment : Fragment() {

    companion object {
        const val tag = "main_fragment"
    }

    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var disposable: Disposable
    private var randomMeals: List<MealNetwork>? = null
    private var searchMeals: List<MealNetwork>? = null
    private var isFavoritesCheck: Boolean = false
    private var adapter = BriefInfoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_layout, container, false)

        val mealList = view.findViewById<RecyclerView>(R.id.main_recycler)
        mealList.adapter = adapter
        mealList.layoutManager = LinearLayoutManager(activity)
        refreshLayout = view.refresh

        whatToDisplay(view)

        refreshLayout.setOnRefreshListener {
            getRandomMealsAndShowThem(view)
        }

        disposable = Observable.create(ObservableOnSubscribe<String> { subscriber ->
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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { text ->
                if (text.isBlank()) {
                    whatToDisplay(view)
                }
                if (text.length > 1) {
                    ApiManager.getSearchMeals(
                        activity = activity,
                        text = text,
                        onSuccess = { meals ->
                            searchMeals = meals
                            adapter.setMeals(searchMeals!!)
                            refreshLayout.isEnabled = true
                            view.toolbar_filter.isEnabled = true
                        },
                        onFailure = { errorMessage ->
                            if (errorMessage == resources.getString(R.string.internet_error)) {
                                Toast.makeText(
                                    activity, getText(R.string.err_lose_connection),
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                getCheckResults(view, true)
                            } else {
                                Toast.makeText(
                                    activity,
                                    "${getText(R.string.err_with_descr)}$errorMessage",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                }
            }
        setToolbarActions(view)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }

    private fun setToolbarActions(view: View) {
        view.toolbar_filter.setOnClickListener {
            (activity as ClickCallback).toFragment(FilterFragment.tag)
        }
        view.toolbar_favorites.setOnCheckedChangeListener { _, isChecked ->
            getCheckResults(view, isChecked)
            if (isChecked) {
                getFavoritesAndShowThem(view)
                refreshLayout.isEnabled = false
            } else {
                refreshLayout.isEnabled = true
                getRandomMealsAndShowThem(view)
            }
        }
    }

    private fun getRandomMealsAndShowThem(view: View) {
        ApiManager.getRandomMeals(
            onSuccess = { meals ->
                adapter.setMeals(meals)
                randomMeals = meals
                refreshLayout.isRefreshing = false
            },
            onFailure = { errorMessage ->
                if (errorMessage == resources.getString(R.string.internet_error)) {
                    Toast.makeText(activity, getText(R.string.err_lose_connection), Toast.LENGTH_LONG).show()
                    view.toolbar_filter.isEnabled = false
                    getCheckResults(view, true)
                    refreshLayout.isRefreshing = false
                } else {
                    Toast.makeText(activity, "${getText(R.string.err_with_descr)}$errorMessage", Toast.LENGTH_LONG)
                        .show()
                }
            }
        )
    }

    private fun getFavoritesAndShowThem(view: View) {
        ApiManager.getFavoriteMeals(
            activity = activity,
            onSuccess = { meals ->
                if (meals.isEmpty()) {
                    getCheckResults(view, false)
                    Toast.makeText(activity, getText(R.string.err_empty_favorites), Toast.LENGTH_LONG).show()
                } else {
                    adapter.setMeals(meals)
                    refreshLayout.isRefreshing = false
                }
            }
        )
    }

    private fun getCheckResults(view: View, check: Boolean) {
        isFavoritesCheck = check
        view.card_toolbar_search.isGone = check
        view.toolbar_favorites.isChecked = check
    }

    private fun whatToDisplay(view: View) {
        when {
            isFavoritesCheck -> {
                getCheckResults(view, isFavoritesCheck)
                getFavoritesAndShowThem(view)
            }
            randomMeals == null -> {
                getRandomMealsAndShowThem(view)
                refreshLayout.isRefreshing = true
            }
            else -> adapter.setMeals(randomMeals!!)
        }
    }
}
