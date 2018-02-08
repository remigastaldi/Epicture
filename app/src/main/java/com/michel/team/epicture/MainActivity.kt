package com.michel.team.epicture

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.TextView
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        var ig = Instagram(this)
        ig.username = "epicture42"
        ig.password = "epitech42"
        ig.prepare()
        ig.login()

        var response = ig.getUserFeed()

        var text = this.findViewById<TextView>(R.id.text_view)

        text.text = response.text

        val file = File(this.filesDir, "log.txt")

        file.printWriter().use {out ->
            out.println(response.text)
        }
        Log.v("MainActivity", response.text)
    }

}
