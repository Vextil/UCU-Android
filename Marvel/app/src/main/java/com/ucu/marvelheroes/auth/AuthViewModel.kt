package com.ucu.marvelheroes.auth

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucu.marvelheroes.data.source.repositories.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val loading = MutableLiveData(false)
    val error = MutableLiveData(AuthError.NONE)
    val state = MutableLiveData(AuthState.LOGGED_OUT)

    init {
        viewModelScope.launch {
            if (authRepository.isLoggedIn()) {
                state.value = AuthState.LOGGED_IN
            }
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            loading.value = true
            error.value = AuthError.NONE
            if (emailIsInvalid(email.value)) {
                error.value = AuthError.INVALID_EMAIL
            } else if (passwordIsInvalid(password.value)) {
                error.value = AuthError.INVALID_PASSWORD
            } else {
                val success = authRepository.login(email.value!!, password.value!!)
                if (success) {
                    state.value = AuthState.LOGGED_IN
                } else {
                    error.value = AuthError.LOGIN_FAILED
                }
            }
            loading.value = false
        }
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            loading.value = true
            error.value = AuthError.NONE
            if (emailIsInvalid(email.value)) {
                error.value = AuthError.INVALID_EMAIL
            } else if (passwordIsInvalid(password.value)) {
                error.value = AuthError.INVALID_PASSWORD
            } else {
                val success = authRepository.register(email.value!!, password.value!!)
                if (success) {
                    state.value = AuthState.LOGGED_IN
                } else {
                    error.value = AuthError.REGISTER_FAILED
                }
            }
            loading.value = false
        }
    }

    fun emailIsInvalid(email: String?): Boolean {
        return email.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun passwordIsInvalid(password: String?): Boolean {
        return password.isNullOrEmpty() || password.length < 6
    }

    enum class AuthState {
        LOGGED_IN,
        LOGGED_OUT
    }

    enum class AuthError {
        NONE,
        INVALID_EMAIL,
        INVALID_PASSWORD,
        LOGIN_FAILED,
        REGISTER_FAILED
    }
}