package com.michel.team.epicture

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

/**
 * Created by hubert_i on 08/02/2018.
 */
class LoginActivity : AppCompatActivity() {
    private var instagram: Instagram? = null
    private var hitcounter = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        InstagramApiContext.init(this)
        instagram = InstagramApiContext.instagram

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        val signInBtn = findViewById<Button>(R.id.signInButton)
        signInBtn.setOnClickListener {
            signIn()
        }

        val userProfileBtn = findViewById<ImageButton>(R.id.user_profile_photo)
        userProfileBtn.setOnClickListener {
            hilterization()
        }


        instagram?.prepare()
        if (instagram!!.isLogin) {
            val intentMain = Intent(this@LoginActivity,
                    MainActivity::class.java)
            this@LoginActivity.startActivity(intentMain)
        }

    }

    fun signIn() {
        val login = findViewById<TextInputEditText>(R.id.login_input)
        val password = findViewById<TextInputEditText>(R.id.password_input)

        println("login " + login.text + " & password " + password.text);

        instagram?.username = "epicture42"
        instagram?.password = "epitech42"
        instagram?.prepare()
        if (!instagram!!.isLogin)
            instagram?.login()
        if (instagram!!.isLogin) {
            val intentMain = Intent(this@LoginActivity,
                    MainActivity::class.java)
            this@LoginActivity.startActivity(intentMain)
        }
        else
            Toast.makeText(this,"Invalid user or password" , Toast.LENGTH_LONG).show()
    }

    fun hilterization() {
        hitcounter = hitcounter + 1;
        if (hitcounter == 5) {
            val btn = findViewById<ImageButton>(R.id.user_profile_photo);
            btn.setImageResource(R.drawable.hilter)
            println("Hilterization activated")
        }
    }
}