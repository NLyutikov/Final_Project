package com.example.finalproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_main.*

const val FRAGMENT_FILTER = 92
const val FRAGMENT_FILTRED_LIST = 94
const val FRAGMENT_DETAIL = 84
const val FRAGMENT_FAVORITE = 88

const val PREV_LIST_FRAGMENT = -2


class MainActivity : FragmentActivity() {

    var currentFragment: Fragment = MainFragment()
        private set(value) {field = value}

    private lateinit var currentListFragment: ListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fragmentId = R.id.fragments
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(fragmentId, currentFragment).commit()

        toFilterView.setOnClickListener {
            toFragment(FRAGMENT_FILTER)
        }

        findViewById<Button>(R.id.goSearch).setOnClickListener {
            if(currentFragment !is FiltredListFragment) {

                //  we cannot display filtred list without filter (required to data loading)
                toFragment(FRAGMENT_FILTRED_LIST, fun(newFragment){
                    (newFragment as FiltredListFragment).filter.ingredients.clear()

                    newFragment.filter.searchWord =
                            findViewById<EditText>(R.id.searchInput)?.text.toString()
                })
            } else {
                (currentFragment as FiltredListFragment).filter.ingredients.clear()
                (currentFragment as FiltredListFragment).filter.searchWord =
                        findViewById<EditText>(R.id.searchInput).text.toString()
            }
        }

        findViewById<Button>(R.id.goFavorite).setOnClickListener {
            toFragment(FRAGMENT_FAVORITE)
        }

        toFragment(0)
    }

    fun toFragment(fragmentId: Int = 0, setParams: ((fr: Fragment) -> Unit)? = null) {
        currentFragment = when(fragmentId) {
            FRAGMENT_FILTER -> Filter()
            FRAGMENT_FILTRED_LIST -> FiltredListFragment()
            FRAGMENT_DETAIL -> DetailFragment()
            FRAGMENT_FAVORITE -> FavoriteListFragment()
            PREV_LIST_FRAGMENT -> currentListFragment
            else -> MainFragment()
        }

        setParams?.invoke(currentFragment)
        if (currentFragment is ListFragment)
            currentListFragment = currentFragment as ListFragment

        supportFragmentManager.beginTransaction().replace(R.id.fragments, currentFragment).commit()
    }

    override fun onBackPressed() {
        when {
            currentFragment is DetailFragment -> toFragment(PREV_LIST_FRAGMENT)
            currentFragment !is MainFragment -> toFragment()
            else -> super.onBackPressed()
        }

    }
}
