package com.michel.team.epicture

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        var ig = Instagram(this)
        ig.username = "pratulisian"
        ig.password = "qwertypoi"
        ig.prepare()
        ig.login()
    }

}
