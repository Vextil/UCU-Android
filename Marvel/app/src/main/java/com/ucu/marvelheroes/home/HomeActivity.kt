package com.ucu.marvelheroes.home

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.ucu.marvelheroes.R


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragmentManager = supportFragmentManager
        fragmentManager.addOnBackStackChangedListener { }
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)
        val fragment = HomeFragment()
        fragmentTransaction.replace(R.id.home_layout, fragment)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }
}