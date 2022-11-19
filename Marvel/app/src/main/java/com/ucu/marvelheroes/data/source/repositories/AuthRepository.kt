package com.ucu.marvelheroes.data.source.repositories

import com.google.firebase.auth.FirebaseAuth
import com.ucu.marvelheroes.data.source.interfaces.IAuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepository(private val firebaseAuth: FirebaseAuth) : IAuthRepository {

    override suspend fun login(email: String, password: String): Boolean {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            return result.user != null
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun register(email: String, password: String): Boolean {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            return result.user != null
        } catch (e: Exception) {
            false
        }
    }

    override fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}