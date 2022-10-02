package uy.edu.ucu.notas

import android.content.Context
import android.content.SharedPreferences

class LoginPref(var context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    companion object {
        const val PREF_NAME = "Login_Preference"
        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "email"
        const val KEY_BIOMETRICS = "biometrics"
    }

    fun userExists(): Boolean {
        return pref.contains(KEY_USERNAME) && pref.contains(KEY_PASSWORD)
    }

    fun biometricsEnabled(): Boolean {
        return pref.getBoolean(KEY_BIOMETRICS, false)
    }

    fun createUser(username: String, password: String, biometrics: Boolean) {
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_PASSWORD, password)
        editor.putBoolean(KEY_BIOMETRICS, biometrics)
        editor.commit()
    }

    fun checkLogin(username: String, password: String): Boolean {
        if (userExists()) {
            val user = pref.getString(KEY_USERNAME, null)
            val pwd = pref.getString(KEY_PASSWORD, null)
            return user == username && pwd == password
        }
        return false
    }

    fun deleteUser() {
        editor.remove(KEY_USERNAME)
        editor.remove(KEY_PASSWORD)
        editor.remove(KEY_BIOMETRICS)
        editor.commit()
    }
}