package com.example.finalproject

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.main_layout.*

@Suppress("IMPLICIT_CAST_TO_ANY")
class MainActivity : FragmentActivity(), ClickCallback {
    //Пока криво работает, не уверен, что правильно понял логику
    override fun onClick(who: View) {
        val transaction = supportFragmentManager.beginTransaction()
        supportFragmentManager.popBackStack()
        val fragment = when (who) {
            toolbar_filter -> FilterFragment()
            else -> MainFragment()
        }
        val fragmentId = when (who) {
            toolbar_filter -> R.layout.filter_layout
            else -> R.id.fragments
        }
        transaction.replace(fragmentId, fragment)
        val bundle = Bundle()
        bundle.putSerializable(fragment.toString(), who.toString())
        fragment.arguments = bundle
        transaction.addToBackStack("fragment")
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentId = R.id.fragments
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(fragmentId, MainFragment()).commit()
    }
}
