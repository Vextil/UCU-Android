package com.ucu.marvelheroes.data.source.repositories

import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ucu.marvelheroes.data.source.interfaces.IAuthRepository
import kotlinx.coroutines.tasks.await


class AuthRepository(private val firebaseAuth: FirebaseAuth) : IAuthRepository {

    override suspend fun login(email: String, password: String): Boolean {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user != null
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun register(email: String, password: String): Boolean {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user != null
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun googleLogin(account: GoogleSignInAccount): Boolean {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            result.user != null
        } catch (e: Exception) {
            false
        }
    }

    override fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}