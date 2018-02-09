package com.michel.team.epicture

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MainActivity : AppCompatActivity() {
    private var instagram = InstagramApiContext.instagram

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val list = ArrayList<Feed>()
        val rView = findViewById<RecyclerView>(R.id.rView)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
        prepareList(list)


//        var response = instagram?.getUserFeed()

//        var text = this.findViewById<TextView>(R.id.text_view)

 //       text.text = response?.text

  /*      val file = File(this.filesDir, "log.txt")

        file.printWriter().use {out ->
            out.println(response?.text)
        }
        println(response?.text)
        Log.v("MainActivity", response?.text)
*/
        val adapter = CustomAdapter(this, list)
        rView.adapter = adapter
        val orientation : Int = resources.configuration.orientation
        rView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            rView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            rView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }

    }
    private fun prepareList(list : ArrayList<Feed>){
        val response = instagram?.getUserFeed()
        val items = response?.jsonObject?.getJSONArray("items")

        val file = File(this.filesDir, "log.txt")

        file.printWriter().use {out ->
            out.println(response?.text)
        }
       var i = 0
        while (!items!!.isNull(i)) {
            val item = items.get(i) as JSONObject

            val images = item.get("image_versions2") as JSONObject
            val candidates = images.getJSONArray("candidates")
            val image1 = candidates.get(0) as JSONObject
            val url = image1.getString("url")

            var text = ""
            val comments = item.getJSONArray("preview_comments")
            if (!comments.isNull(0)) {
                val comment = comments.get(0) as JSONObject
                text = comment.getString("text")
            }

            val likes = item.getInt("like_count")

            list.add(Feed(text, likes, url))

            i++
        }
       // val item = items?.get(0) as JSONObject
     /*   val images = item.get("image_versions2") as JSONObject
        val candidates = images.getJSONArray("candidates")
        val image1 = candidates.get(0) as JSONObject
        val url = image1.get("url") as String
        println(url)
        list.add(Feed("What is", 2, url))
        list.add(Feed("No No", 2, "https://picsum.photos/1920/1080/?random"))
        list.add(Feed("test", 2, "https://picsum.photos/600/1200/?random"))
        */
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_action -> {
                logoutDialog()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun logoutDialog() {
        val builder = AlertDialog.Builder(this)

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.logout_layout, null)

        builder.setView(dialogView)

        val okButton = dialogView.findViewById<Button>(R.id.logout_ok_button)
        val cancelButton = dialogView.findViewById<Button>(R.id.logout_cancel_button)

        val dialog = builder.create()

        okButton.setOnClickListener {
            dialog.dismiss()
            instagram?.logout()
            val intentMain = Intent(this@MainActivity,
                    LoginActivity::class.java)
            this@MainActivity.startActivity(intentMain)
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }
}
