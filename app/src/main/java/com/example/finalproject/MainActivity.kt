package com.example.finalproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_main.*

const val FRAGMENT_LIST = 91
const val FRAGMENT_FILTER = 92
const val FRAGMENT_FILTRED_LIST = 92

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fragmentId = R.id.fragments
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(fragmentId, MainFragment()).commit()



        toFilterView.setOnClickListener {
            toFragment(FRAGMENT_FILTER)
        }
    }

    fun toFragment(fragmentId: Int = 0, setParams: ((fr: Fragment) -> Unit)? = null) {
        val newFragment: Fragment = when(fragmentId) {
            FRAGMENT_FILTER -> Filter()
            FRAGMENT_FILTRED_LIST -> FiltredListFragment()
            else -> MainFragment()
        }

        Log.d("current", fragmentId.toString())
        if(setParams != null)
            setParams.invoke(newFragment)

        supportFragmentManager.beginTransaction().replace(R.id.fragments, newFragment).commit()
    }

}
