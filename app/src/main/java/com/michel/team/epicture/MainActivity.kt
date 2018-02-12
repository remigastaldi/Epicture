package com.michel.team.epicture

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*


enum class STATUS {
    FEED,
    SEARCH,
    ADD,
    FAVORITES,
    PROFILE
}

class MainActivity : AppCompatActivity() {
    private var instagram = InstagramApiContext.instagram
    private val list = ArrayList<Feed>()
    private var adapter = CustomAdapter(this, list)
    private var status = STATUS.FEED
    private var searchString = ""
    private var randomSearch = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rView = findViewById<RecyclerView>(R.id.rView)

        val homeButton = findViewById<ImageView>(R.id.action_bar_home_button)
        homeButton.background.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.MULTIPLY)
        val backButtonHomeButton = findViewById<LinearLayout>(R.id.back_button_action_bar_home_button)
        backButtonHomeButton.setOnClickListener {
            prepareFeedList()
        }

        val backButtonSearchButton = findViewById<LinearLayout>(R.id.back_button_action_bar_search_button)
        backButtonSearchButton.setOnClickListener {
            randomSearch = true
            changeStatus(status, STATUS.SEARCH)
            val source = "abcdefghijklmnopqrstuvwxyz"
            searchString = source[Math.floor(Math.random() * source.length).toInt()].toString()
            prepareSearchList(searchString)
        }

        val backButtonFavoritesButton = findViewById<LinearLayout>(R.id.back_button_action_bar_favorite_button)
        backButtonFavoritesButton.setOnClickListener {
             prepareFavoritesList()
        }

        val backButtonUserProfileButton = findViewById<LinearLayout>(R.id.back_button_action_bar_user_profile_button)
        backButtonUserProfileButton.setOnClickListener {
            prepareUserFeedList()
        }


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

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            when (status) {
                STATUS.FEED -> {
                    prepareFeedList()
                }

                STATUS.SEARCH -> {
                    if (!randomSearch) {
                        prepareSearchList(searchString)
                    } else {
                        val source = "abcdefghijklmnopqrstuvwxyz"
                        searchString = source[Math.floor(Math.random() * source.length).toInt()].toString()
                        prepareSearchList(searchString)
                    }
                }

                STATUS.ADD -> {
                }

                STATUS.FAVORITES -> {
                    prepareFavoritesList()
                }

                STATUS.PROFILE -> {
                    prepareUserFeedList()
                }
            }
        }

        changeMenuBarToHome()
    }


    private fun clearList() {
        val size = list.size
        list.clear()
        adapter.notifyItemRangeRemoved(0, size)
    }

    override fun onStart() {
        prepareFeedList()
        super.onStart()
    }

    private fun changeMenuBarToHome() {
        val action = supportActionBar
        action?.setDisplayShowCustomEnabled(true)
        action?.setCustomView(R.layout.custom_menu)

        val textView = findViewById<TextView>(R.id.app_title)
        val typeface = Typeface.createFromAsset(assets, "fonts/Billabong.ttf")
        textView.typeface = typeface

        // Action Bar handler
        val view = this.supportActionBar?.customView

        val exitButton = view?.findViewById(R.id.logout_action) as ImageButton
        exitButton.setOnClickListener({ v ->
            logoutDialog()
        })

        val photoButton = view.findViewById(R.id.camera_action) as ImageButton
        photoButton.setOnClickListener({ v ->
            Toast.makeText(this,"Camera clicked" , Toast.LENGTH_LONG).show()
        })
    }

    private fun changeMenuBarToSearch() {
        // Set Custom Menu
        val action = supportActionBar
        action?.setDisplayShowCustomEnabled(true)
        action?.setCustomView(R.layout.search_menu)

        val searchInput = findViewById<EditText>(R.id.search_input)
        val cancelSearchButton = findViewById<Button>(R.id.cancel_search_button)
        val searchInputForm = findViewById<RelativeLayout>(R.id.search_form)

        searchInputForm.setOnClickListener {
            searchInput.requestFocus()
            val lManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            lManager.showSoftInput(searchInput, 0)
        }

        searchInput.setOnFocusChangeListener { v, hasFocus ->
            cancelSearchButton.visibility = Button.VISIBLE
            searchInput.gravity = Gravity.LEFT
            searchInputForm.gravity = Gravity.LEFT
        }

        searchInput.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!searchInput.text.isEmpty()) {
                    randomSearch = false
                    searchString = searchInput.text.toString()
                    prepareSearchList(searchString)
                }
                val view = this.currentFocus
                if (view != null) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                searchInput.clearFocus()
                return@OnEditorActionListener true
            }
            false
        })

        cancelSearchButton.setOnClickListener {
            searchInput.text.clear()
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            searchInput.clearFocus()
            prepareFeedList()
            cancelSearchButton.visibility = Button.GONE
            searchInput.gravity = Gravity.CENTER
            searchInputForm.gravity = Gravity.CENTER

        }
    }

    private fun changeStatus(from: STATUS, to: STATUS) {
        val userProfileButton = findViewById<ImageView>(R.id.action_bar_user_profile_button)
        val searchButton = findViewById<ImageView>(R.id.action_bar_search_button)
        val homeButton = findViewById<ImageView>(R.id.action_bar_home_button)
        val favoritesButton = findViewById<ImageView>(R.id.action_bar_favorite_button)

        when (from) {
            STATUS.FEED -> {
                homeButton.background.clearColorFilter()
            }

            STATUS.SEARCH -> {
                searchButton.background.clearColorFilter()
                changeMenuBarToHome()
            }

            STATUS.ADD -> {
            }

            STATUS.FAVORITES -> {
                favoritesButton.background.clearColorFilter()
            }

            STATUS.PROFILE -> {
                userProfileButton.background.clearColorFilter()
            }
        }

        when (to) {
            STATUS.FEED -> {
                homeButton.background.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.MULTIPLY)
            }

            STATUS.SEARCH -> {
                searchButton.background.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.MULTIPLY)
                changeMenuBarToSearch()
            }

            STATUS.ADD -> {
            }

            STATUS.FAVORITES -> {
                favoritesButton.background.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.MULTIPLY)
            }

            STATUS.PROFILE -> {
                userProfileButton.background.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.MULTIPLY)
            }
        }

        status = to
    }

    private fun addCard(item: JSONObject) {

        val hasLiked = if (item.isNull("has_liked") && status != STATUS.FAVORITES) {
            false
        } else if (status == STATUS.FAVORITES) {
            true
        }
        else {
            item.getBoolean("has_liked")
        }

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
                text = "<b>" + comment.getJSONObject("user").getString("username") + "</b> " +  comment.getString("text")
            }
        }
        else {
            val caption = item.get("caption") as JSONObject
            if (!caption.isNull("text")) {
                text = "<b>" + username + "</b> " +  caption.getString("text")
            }
        }


        val likes = item.getInt("like_count")

        if (item.getInt("media_type") == 2) {
            val urlvideo = item.getJSONArray("video_versions").get(0) as JSONObject
            val hasAudio = !item.isNull("has_audio")
            list.add(Feed(username, user_pic, imageWidth, imageHeight, text, likes, hasLiked, urlvideo.getString("url"), 1, hasAudio))

        } else {
            list.add(Feed(username, user_pic, imageWidth, imageHeight, text, likes, hasLiked, url, 0, false))
        }

        this.runOnUiThread({
            adapter.notifyItemInserted(list.size)
        })
    }

    private fun prepareUserFeedList() {
        changeStatus(status, STATUS.PROFILE)
        clearList()

        Thread(Runnable {
            val response = InstagramApiContext.instagram?.getUserFeed()
            val items = response?.jsonObject?.getJSONArray("items")


            var i = 0
            while (!items!!.isNull(i)) {
                val item = items.get(i) as JSONObject

                if (item.isNull("image_versions2")) {
                    i++
                    continue
                }
                addCard(item)
                i++
            }
            this.runOnUiThread({
                val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refresh_layout)
                swipeRefreshLayout.isRefreshing = false
            })
        }).start()
    }

    private fun prepareSearchList(param: String) {
        clearList()
        Thread(Runnable {
            val response = instagram?.tagFeed(param)
            val items = response?.jsonObject?.getJSONArray("items") as JSONArray


            var i = 0
            while (!items.isNull(i)) {
                val item = items.get(i) as JSONObject

                if (item.isNull("image_versions2")) {
                    i++
                    continue
                }

                addCard(item)
                i++
            }

            this.runOnUiThread({
                val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refresh_layout)
                swipeRefreshLayout.isRefreshing = false
            })
        }).start()
    }

    private fun prepareFeedList() {
        changeStatus(status, STATUS.FEED)
        clearList()

        Thread(Runnable {
            val response  = instagram?.getTimelineFeed()
            val items = response?.jsonObject?.getJSONArray("feed_items") as JSONArray

            var i = 0
            while (!items.isNull(i)) {
                val itemPack = items.get(i) as JSONObject

                if (itemPack.isNull("media_or_ad")) {
                    i++
                    continue
                }

                val item = itemPack.get("media_or_ad") as JSONObject

                addCard(item)
                i++
            }

            this.runOnUiThread({
                val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refresh_layout)
                swipeRefreshLayout.isRefreshing = false
            })
        }).start()
    }

    private fun prepareFavoritesList() {
        changeStatus(status, STATUS.FAVORITES)
        clearList()

        Thread(Runnable {
            val response  = instagram?.selfLikedFeed()
            val items = response?.jsonObject?.getJSONArray("items") as JSONArray

            File(filesDir,"log.txt").printWriter().use { out ->
                out.println(response.text)
            }

            var i = 0
            while (!items.isNull(i)) {
                val item = items.get(i) as JSONObject

                if (item.isNull("image_versions2")) {
                    i++
                    continue
                }

                addCard(item)
                i++
            }

            this.runOnUiThread({
                val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refresh_layout)
                swipeRefreshLayout.isRefreshing = false
            })
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
