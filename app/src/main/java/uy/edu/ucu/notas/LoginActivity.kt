package uy.edu.ucu.notas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.content.res.TypedArrayUtils.getString
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val pref by lazy { LoginPref(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (pref.userExists()) {
            setupLogin()
        } else {
            setupSignUp()
        }

    }

    private fun setupLogin() {
        main_title.text = getString(R.string.access)
        btnlogin.text = getString(R.string.access)
        biometricSwitch.visibility = View.INVISIBLE
        btnlogin.setOnClickListener {
            val username = username.text.toString()
            val password = password.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.please_type_login_info),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (pref.checkLogin(username, password)) {
                    goToNotesList()
                } else {
                    Toast.makeText(this, getString(R.string.incorrect_login_info), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        if (pref.biometricsEnabled()) {
            biometricLogin()
        }
    }

    private fun setupSignUp() {
        main_title.text = getString(R.string.create_account)
        btnlogin.text = getString(R.string.create)
        biometricSwitch.visibility = View.VISIBLE
        btnlogin.setOnClickListener {
            val username = username.text.toString()
            val password = password.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.please_type_login_info),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                pref.createUser(username, password, biometricSwitch.isChecked)
                if (biometricSwitch.isChecked) {
                    configureBiometrics()
                }
                goToNotesList()
            }
        }
    }

    private fun goToNotesList() {
        val i = Intent(applicationContext, NotesListActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
        finish()
    }

    private fun configureBiometrics() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val biometricManager = BiometricManager.from(this)
            when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
                BiometricManager.BIOMETRIC_SUCCESS ->
                    Log.d("Notas", "App can authenticate using biometrics.")
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                    Log.e("Notas", "No biometric features available on this device.")
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                    Log.e("Notas", "Biometric features are currently unavailable.")
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    Log.e(
                        "Notas", "The user hasn't associated " +
                                "any biometric credentials with their account."
                    )
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                        )
                    }
                    startActivity(enrollIntent)
                }
                else -> {
                    Log.e("Notas", "Biometric authentication error")
                }
            }
        }
    }

    private fun biometricLogin() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.e(
                        "Notas", "Authentication error: $errString"
                    )
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    goToNotesList()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.e(
                        "Notas", "Authentication failed"
                    )
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.access_your_notes))
            .setSubtitle(getString(R.string.use_biometrics_to_access))
            .setNegativeButtonText(getString(R.string.use_login_info))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }


}