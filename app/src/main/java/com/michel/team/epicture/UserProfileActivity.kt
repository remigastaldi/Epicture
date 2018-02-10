package com.michel.team.epicture

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import com.michel.team.epicture.InstagramApiContext.instagram
import org.json.JSONObject
import java.io.File

/**
 * Created by gastal_r on 2/10/18.
 */

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val list = ArrayList<Feed>()
        val rView = findViewById<RecyclerView>(R.id.rView)

        val userProfileButton = findViewById<ImageButton>(R.id.action_bar_home_button)

        userProfileButton.setOnClickListener {
            val intentUserProfile = Intent(this,
                    MainActivity::class.java)
            this.startActivity(intentUserProfile)
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
        prepareList(list)


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

        var i = 0
        while (!items!!.isNull(i)) {
            val item = items.get(i) as JSONObject

            if (item.isNull("image_versions2"))
                continue

            val hasLiked = item.getBoolean("has_liked")

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

            list.add(Feed(text, likes, hasLiked, url))

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
            val intentMain = Intent(this,
                    LoginActivity::class.java)
            this.startActivity(intentMain)
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }
}