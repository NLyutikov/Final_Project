package com.example.finalproject

<<<<<<< HEAD
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
=======
>>>>>>> origin/main-fragment
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fragmentId = R.id.fragments
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(fragmentId, MainFragment()).commit()

    }
}
