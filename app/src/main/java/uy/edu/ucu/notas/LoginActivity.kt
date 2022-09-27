package uy.edu.ucu.notas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private  lateinit var  username: EditText
    private lateinit var password: EditText
    private  lateinit var btnLogin : Button

    lateinit var session : LoginPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        session = LoginPref(this)
        if(session.isLoggedIn()){
            var i : Intent = Intent(applicationContext, NotesListActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()

        }
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        btnLogin = findViewById(R.id.btnlogin)

        btnLogin.setOnClickListener{
            var user = username.text.toString().trim()
            var pwd = password.text.toString().trim()

            if(user.isEmpty() || pwd.isEmpty()){
                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
            }else{
                session.createLoginSession(user,pwd)
                var i : Intent = Intent(applicationContext, NotesListActivity::class.java)
                startActivity(i)
                finish()

            }
        }
        themeSwitch.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

    }


}