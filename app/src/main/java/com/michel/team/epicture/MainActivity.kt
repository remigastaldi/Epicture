package com.michel.team.epicture

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.TextView
import java.io.File
import android.content.Intent
import android.content.res.Configuration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    private var instagram = InstagramApiContext.instagram

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val list = ArrayList<Feed>();
        prepareList(list);
        val rView = findViewById(R.id.rView) as RecyclerView;

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        
        var response = instagram?.getUserFeed()

//        var text = this.findViewById<TextView>(R.id.text_view)

 //       text.text = response?.text

        val file = File(this.filesDir, "log.txt")

        file.printWriter().use {out ->
            out.println(response?.text)
        }
        println(response?.text)
        Log.v("MainActivity", response?.text)

        val adapter = CustomAdapter(this, list)
        rView.adapter = adapter;
        val orientation : Int = getResources().getConfiguration().orientation
        rView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            rView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            rView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }

    }
    private fun prepareList(list : ArrayList<Feed>){
        list.add(Feed("What is", 2, R.drawable.login_background))
        list.add(Feed("No No", 2, R.drawable.login_background))
        list.add(Feed("test", 2, R.drawable.login_background))
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
