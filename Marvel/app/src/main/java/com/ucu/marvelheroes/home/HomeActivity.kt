package com.ucu.marvelheroes.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ucu.marvelheroes.R

class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this, HomeViewModel.Factory())[HomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        viewModel.characters.observe(this) { characters ->
            characters.forEach {
                Log.v("CHARACTER NAME", "Character name is: ${it.name}")
            }
        }
        viewModel.refreshCharacters()
    }

    override fun onPause() {
        super.onPause()
        viewModel.characters.removeObservers(this)
    }
}