package uy.edu.ucu.notas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private val pref by lazy { LoginPref(this) }
    private val GOOGLE_SIGN_IN = 100

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

        google_sign_in_button.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


            val signInIntent = mGoogleSignInClient.signInIntent
            mGoogleSignInClient.signOut();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
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

                if (!isValidEmail(username)) {
                    Toast.makeText(this, "Mail invalido", Toast.LENGTH_SHORT).show()
                } else {
                    if (isValidPassword(password)) {
                        pref.createUser(username, password, biometricSwitch.isChecked)
                        if (biometricSwitch.isChecked) {
                            configureBiometrics()
                        }
                        goToNotesList()
                    } else {
                        Toast.makeText(this, "Ingrese una contraseña de al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                    }

                }

            }
        }
        google_sign_in_button.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = mGoogleSignInClient.signInIntent
            mGoogleSignInClient.signOut();

            startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
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


    private fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    // valid password
    private fun isValidPassword(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && target!!.length >= 6
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    if (pref.userExists()){
                        if (pref.checkLogin(account.email!!, account.id!!)) {

                            goToNotesList()
                        } else {
                            Toast.makeText(this, getString(R.string.incorrect_login_info), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }else {
                        pref.createUser(account.email!!, account.id!!, biometricSwitch.isChecked)
                        if (biometricSwitch.isChecked) {
                            configureBiometrics()
                        }


                        goToNotesList()
                    }

                }
            } catch (e: ApiException) {
                Log.w("Note", "Google sign in failed", e)
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