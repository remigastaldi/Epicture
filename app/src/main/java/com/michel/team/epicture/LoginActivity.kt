package com.michel.team.epicture

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.graphics.Typeface
import android.widget.*
import pl.droidsonroids.gif.GifImageView


/**
 * Created by hubert_i on 08/02/2018.
 */

class LoginActivity : AppCompatActivity() {
    private var instagram: Instagram? = null
    private var hitcounter = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val textView = findViewById(R.id.login_title) as TextView
        val typeface = Typeface.createFromAsset(assets, "fonts/Billabong.ttf")
        textView.typeface = typeface

        InstagramApiContext.init(this)
        instagram = InstagramApiContext.instagram

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        val signInBtn = findViewById<Button>(R.id.signInButton)
        signInBtn.setOnClickListener {
            signIn()
        }

        try {
            instagram?.prepare()
            if (instagram!!.isLogin) {
                val intentMain = Intent(this@LoginActivity,
                        MainActivity::class.java)
                this@LoginActivity.startActivity(intentMain)
            }
        } catch (name: Exception) {
            Toast.makeText(this,"Login error, please check your network !" , Toast.LENGTH_LONG).show()

        }


    }

    fun signIn() {
        val login = findViewById<EditText>(R.id.login_input)
        val password = findViewById<EditText>(R.id.password_input)
        val btn = findViewById<Button>(R.id.signInButton)
        val loading = findViewById<GifImageView>(R.id.loading)
        val btnText = btn.text

        println("login " + login.text + " & password " + password.text)
        if (!login.text.isEmpty() && !password.text.isEmpty()) {
            instagram?.username = login.text.toString()
            instagram?.password = password.text.toString()
        } else {
            Toast.makeText(this, "No login or password provided", Toast.LENGTH_SHORT).show()
            return
        }

        btn.text = ""
        loading.visibility = GifImageView.VISIBLE
        loading.bringToFront()

        Thread({
            instagram?.prepare()
            if (!instagram!!.isLogin)
                instagram?.login()

            this.runOnUiThread({
                loading.visibility = GifImageView.GONE
                btn.text = btnText
                if (instagram!!.isLogin) {
                    val intentMain = Intent(this@LoginActivity,
                            MainActivity::class.java)
                    this@LoginActivity.startActivity(intentMain)
                }
                else
                    Toast.makeText(this,"Invalid user or password" , Toast.LENGTH_LONG).show()
            })
        }).start()

    }

}