package com.example.finalproject

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

const val FRAGMENT_FILTER = 92
const val FRAGMENT_FILTRED_LIST = 94
const val FRAGMENT_DETAIL = 84
const val FRAGMENT_FAVORITE = 88

const val PREV_LIST_FRAGMENT = -2


class MainActivity : AppCompatActivity(), ClickCallback {

    var currentFragment: Fragment = MainFragment()
        private set(value) {
            field = value
        }

    private lateinit var currentListFragment: ListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toFragment(0)
    }

    fun toFragment(fragmentId: Int = 0, setParams: ((fr: Fragment) -> Unit)? = null) {
        currentFragment = when (fragmentId) {
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

    //  not working
    override fun onClick(who: View) {
        when (who.id) {
            R.id.toolbar_favorites -> toFragment(FRAGMENT_FAVORITE)
            R.id.toolbar_filter -> toFragment(FRAGMENT_FILTER)
        }
    }
}

interface ClickCallback {
    fun onClick(who: View)
}