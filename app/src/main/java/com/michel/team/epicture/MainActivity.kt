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
    private var instagram = InstagramApiContext.instagram

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)


        val response = instagram?.getUserFeed()

        val text = this.findViewById<TextView>(R.id.text_view)

        text.text = response?.text

        val file = File(this.filesDir, "log.txt")

        file.printWriter().use {out ->
            out.println(response?.text)
        }
        Log.v("MainActivity", response?.text)



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_action -> {
                instagram?.logout()
                val intentMain = Intent(this@MainActivity,
                        LoginActivity::class.java)
                this@MainActivity.startActivity(intentMain)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
