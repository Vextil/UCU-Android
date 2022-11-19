package com.ucu.marvelheroes.data.source.interfaces

import com.firebase.ui.auth.data.model.Resource
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser

interface IAuthRepository {
    suspend fun login(email: String, password: String): Boolean
    suspend fun register(email: String, password: String): Boolean
    suspend fun googleLogin(account: GoogleSignInAccount): Boolean
    fun isLoggedIn(): Boolean
}