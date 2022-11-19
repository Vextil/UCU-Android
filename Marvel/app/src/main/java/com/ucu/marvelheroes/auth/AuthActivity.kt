package com.ucu.marvelheroes.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.databinding.ActivityAuthBinding
import com.ucu.marvelheroes.home.HomeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class AuthActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModel()
    private lateinit var binding: ActivityAuthBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    private var googleSignInResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(result.data!!)
                if (signInResult?.isSuccess == true) {
                    viewModel.setGoogleAccount(signInResult.signInAccount!!)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        viewModel.state.observe(this) {
            if (it == AuthViewModel.AuthState.LOGGED_IN) {
                goToHome()
            }
        }

        viewModel.googleLogin.observe(this) {
            if (it) {
                openGoogleSignIn()
            }
        }
    }

    private fun openGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInResultLauncher.launch(signInIntent)
    }

    private fun goToHome() {
        val i = Intent(applicationContext, HomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
        finish()
    }

}