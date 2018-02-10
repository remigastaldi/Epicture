package com.michel.team.epicture

import android.app.ActionBar
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.squareup.picasso.Downloader
import khttp.responses.Response
import org.json.JSONObject
import java.io.File
import kotlin.concurrent.thread
import android.text.Spannable
import android.text.style.TypefaceSpan
import android.text.SpannableString
import android.view.View
import android.widget.*


class MainActivity : AppCompatActivity() {
    private var instagram = InstagramApiContext.instagram
    val list = ArrayList<Feed>()
    private var adapter = CustomAdapter(this, list)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rView = findViewById<RecyclerView>(R.id.rView)

        // Set Custom Menu
        val action = getSupportActionBar()
        action?.setDisplayShowCustomEnabled(true)
        action?.setCustomView(R.layout.custom_menu)

        val textView = findViewById(R.id.app_title) as TextView
        val typeface = Typeface.createFromAsset(assets, "fonts/Billabong.ttf")
        textView.typeface = typeface

        val userProfileButton = findViewById<ImageView>(R.id.action_bar_user_profile_button)
        userProfileButton.background.clearColorFilter()
        val backButtonUserProfileButton = findViewById<LinearLayout>(R.id.back_button_action_bar_user_profile_button)
        backButtonUserProfileButton.setOnClickListener {
            val intentUserProfile = Intent(this,
                    UserProfileActivity::class.java)
            intentUserProfile.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            this.startActivity(intentUserProfile)
        }

        val homeButton = findViewById<ImageView>(R.id.action_bar_home_button)
        homeButton.background.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.MULTIPLY)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        rView.adapter = adapter
        val orientation: Int = resources.configuration.orientation
        rView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            rView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }

        // Action Bar handler
        val view = supportActionBar!!.customView

        val exitButton = view.findViewById(R.id.logout_action) as ImageButton
        exitButton.setOnClickListener({ v ->
            logoutDialog()
        })

        val photoButton = view.findViewById(R.id.camera_action) as ImageButton
        photoButton.setOnClickListener({ v ->
            Toast.makeText(this,"Camera clicked" , Toast.LENGTH_LONG).show()
        })
    }

    override fun onStart() {

        prepareList()
        super.onStart()
    }

    private fun prepareList() {

        Thread(Runnable {
            var response = instagram?.getTimelineFeed()


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

                val username = item.getJSONObject("user").getString("username")
                val user_pic = item.getJSONObject("user").getString("profile_pic_url")
                var text = ""
                if (item.isNull("caption")) {
                    val comments = item.getJSONArray("preview_comments")
                    if (!comments.isNull(0)) {
                        val comment = comments.get(0) as JSONObject
                        text = "<b>" + comment.getJSONObject("user").getString("username") + "</b> " + comment.getString("text")
                    }
                } else {
                    val caption = item.get("caption") as JSONObject
                    if (!caption.isNull("text")) {
                        text = "<b>" + username + "</b> " + caption.getString("text")
                    }
                }

                val likes = item.getInt("like_count")

                if (item.getInt("media_type") == 2) {
                    val urlvideo = item.getJSONArray("video_versions").get(0) as JSONObject
                    list.add(Feed(username, user_pic, imageWidth, imageHeight, text, likes, hasLiked, urlvideo.getString("url"), 1, item.getBoolean("has_audio")))

                } else {
                    list.add(Feed(username, user_pic, imageWidth, imageHeight, text, likes, hasLiked, url, 0, false))
                }

                this.runOnUiThread({
                    adapter.notifyItemInserted(list.size)
                })
                i++
            }
        }).start()
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
