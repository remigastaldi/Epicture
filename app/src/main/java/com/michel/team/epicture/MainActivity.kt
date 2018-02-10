package com.michel.team.epicture

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import org.json.JSONObject
import java.io.File

class MainActivity : AppCompatActivity() {
    private var instagram = InstagramApiContext.instagram
    val list = ArrayList<Feed>()
    private var adapter = CustomAdapter(this, list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rView = findViewById<RecyclerView>(R.id.rView)

        val userProfileButton = findViewById<ImageButton>(R.id.action_bar_user_profile_button)
        userProfileButton.background.clearColorFilter()
        userProfileButton.setOnClickListener {
            val intentUserProfile = Intent(this,
                    UserProfileActivity::class.java)
            this.startActivity(intentUserProfile)
        }

        val homeButton = findViewById<ImageButton>(R.id.action_bar_home_button)
        homeButton.background.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.MULTIPLY)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

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

    override fun onStart() {
        prepareList()
        super.onStart()
    }

    private fun prepareList(){

        val response = instagram?.getTimelineFeed()
        val file = File(this.filesDir, "log.txt")

        file.printWriter().use {out ->
            out.println(response?.text)
        }

        val items = response?.jsonObject?.getJSONArray("feed_items")


       var i = 0
        while (!items!!.isNull(i)) {
            val itemPack = items.get(i) as JSONObject

            if (itemPack.isNull("media_or_ad")) {
                i++
                continue
            }

            val item = itemPack.get("media_or_ad") as JSONObject

            val hasLiked = item.getBoolean("has_liked")

            val images = item.get("image_versions2") as JSONObject
            val candidates = images.getJSONArray("candidates")
            val image1 = candidates.get(0) as JSONObject
            val url = image1.getString("url")
            val imageWidth = image1.getInt("width")
            val imageHeight = image1.getInt("height")

            var text = ""
            if (item.isNull("caption")) {
                val comments = item.getJSONArray("preview_comments")
                if (!comments.isNull(0)) {
                    val comment = comments.get(0) as JSONObject
                    text = comment.getString("text")
                }
            }
            else {
                val caption = item.get("caption") as JSONObject
                if (!caption.isNull("text")) {
                    text = "<b>" + caption.getJSONObject("user").getString("username") + "</b> " +  caption.getString("text")
                }
            }

            val likes = item.getInt("like_count")

            list.add(Feed(imageWidth, imageHeight, text, likes, hasLiked, url))
            adapter.notifyItemInserted(list.size)
            i++
        }
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
