package com.ucu.marvelheroes.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ucu.marvelheroes.R
import com.ucu.marvelheroes.home.HomeActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText


class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val emailInput by lazy { findViewById<TextInputEditText>(R.id.et_email) }
    private val passwordInput by lazy { findViewById<TextInputEditText>(R.id.et_email) }
    private val loginButton by lazy { findViewById<MaterialButton>(R.id.btn_login) }
    private val registerButton by lazy { findViewById<MaterialButton>(R.id.btn_register) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_auth)

        loginButton.setOnClickListener {
            signIn(emailInput.text.toString(), passwordInput.text.toString())
        }
        registerButton.setOnClickListener {
            createAccount(emailInput.text.toString(), passwordInput.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            goToHome()
        }
    }

    fun goToHome() {
        val i = Intent(applicationContext, HomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
        finish()
    }

    private fun createAccount(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(baseContext, "Email or password is empty.",
                Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(baseContext, "Registering...", Toast.LENGTH_SHORT).show()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with tjjnhe signed-in user's information
                    Log.d("MARVEL", "createUserWithEmail:success")
                    Toast.makeText(baseContext, "Register success.",
                        Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("MARVEL", "createUserWithEmail:unsuccessful", task.exception)
                    Toast.makeText(baseContext, "Authentication was not successful.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }.addOnFailureListener(this) {
                Log.w("MARVEL", "createUserWithEmail:failure", it)
                Toast.makeText(baseContext, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()
            }
    }


    private fun signIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(baseContext, "Email or password is empty.",
                Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(baseContext, "Logging in...", Toast.LENGTH_SHORT).show()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("MARVEL", "signInWithEmail:success")
                    Toast.makeText(baseContext, "Login success.",
                        Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("MARVEL", "signInWithEmail:unsuccessful", task.exception)
                    Toast.makeText(baseContext, "Authentication not successful.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }.addOnFailureListener(this) {
                Log.w("MARVEL", "signInWithEmail:failure", it)
                Toast.makeText(baseContext, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()
            }
    }

}