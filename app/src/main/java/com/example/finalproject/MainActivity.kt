package com.example.finalproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity(), ClickCallback {

    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments, MainFragment()).commit()
    }

    override fun toFragment(fragmentId: String) {
        currentFragment = when (fragmentId) {
            FilterFragment.tag -> FilterFragment()
            MainFragment.tag -> MainFragment()
            else -> MainFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments, currentFragment)
            .addToBackStack(fragmentId)
            .commit()
    }

    override fun onClick(meal: MealNetwork) {
        val transaction = supportFragmentManager.beginTransaction()
        supportFragmentManager.popBackStack()
        val fragment = DetailFragment()
        val fragmentId = R.id.fragments
        transaction.replace(fragmentId, fragment)
        val bundle = Bundle()
        bundle.putSerializable(DetailFragment.MEAL_KEY, meal)
        fragment.arguments = bundle
        transaction.addToBackStack(DetailFragment.tag)
        transaction.commit()
    }
}

interface ClickCallback {
    fun onClick(meal: MealNetwork)
    fun toFragment(fragmentId: String)
}
