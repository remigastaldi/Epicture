package com.michel.team.epicture

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

/**
 * Created by hubert_i on 08/02/2018.
 */
class LoginActivity : AppCompatActivity() {
    val ig = Instagram(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        val btn_click_me = findViewById<Button>(R.id.signInButton)
        btn_click_me.setOnClickListener {
            signIn()
        }

        ig.prepare()
        if (ig.isLogin) {
            val intentMain = Intent(this@LoginActivity,
                    MainActivity::class.java)
            this@LoginActivity.startActivity(intentMain)
        }

    }

    fun signIn() {
        val login = findViewById<EditText>(R.id.login_input)
        val password = findViewById<EditText>(R.id.password_input)

        Toast.makeText(this, "login " + login.text + " & password " + password.text , Toast.LENGTH_LONG).show()
        println("login " + login.text + " & password " + password.text);

        ig.username = "epicture42"
        ig.password = "epitech42"
        ig.prepare()
        if (!ig.isLogin)
            ig.login()
        if (ig.isLogin) {
            val intentMain = Intent(this@LoginActivity,
                    MainActivity::class.java)
            this@LoginActivity.startActivity(intentMain)
        }
    }
}