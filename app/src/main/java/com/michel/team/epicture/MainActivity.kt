package com.michel.team.epicture

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.TextView
import java.io.File
import android.content.Intent
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    val ig = Instagram(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        ig.username = "epicture42"
        ig.password = "epitech42"
        ig.prepare()
        ig.login()
        /*if (!ig.isLogin) {
                  val intentMain = Intent(this@MainActivity,
                          LoginActivity::class.java)
                  this@MainActivity.startActivity(intentMain)
              }*/ // TODO


        var response = ig.getUserFeed()

        var text = this.findViewById<TextView>(R.id.text_view)

        text.text = response.text

        val file = File(this.filesDir, "log.txt")

        file.printWriter().use {out ->
            out.println(response.text)
        }
        Log.v("MainActivity", response.text)



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.logout_action -> {
                ig.logout()
                val intentMain = Intent(this@MainActivity,
                        LoginActivity::class.java)
                this@MainActivity.startActivity(intentMain)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
