package com.ucu.marvelheroes.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.databinding.ActivityAuthBinding
import com.ucu.marvelheroes.home.HomeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModel()
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.state.observe(this) {
            if (it == AuthViewModel.AuthState.LOGGED_IN) {
                goToHome()
            }
        }
    }

    fun goToHome() {
        val i = Intent(applicationContext, HomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
        finish()
    }

}