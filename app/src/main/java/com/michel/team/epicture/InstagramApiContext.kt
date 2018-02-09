package com.michel.team.epicture

/**
 * Created by gastal_r on 2/9/18.
 */

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object InstagramApiContext {
    var context: Context? = null
    var instagram: Instagram? = null

    fun init(context: Context) {
        this.context = context.applicationContext
        this.instagram = Instagram(context.applicationContext)
    }
}