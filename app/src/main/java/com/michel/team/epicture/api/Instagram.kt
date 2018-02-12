package com.michel.team.epicture

import android.content.Context
import khttp.responses.Response
import org.json.JSONObject
import java.io.File


class Instagram(private var context: Context) {
    var username: String = "uname"
    var password: String = "pass"
    var deviceId: String = "xxxx"
    var uuid: String = "xxxx"
    var isLogin = false
    var ds_user_id = ""
    var token: String = "-"
    var rankToken: String = "-"
    var Req = Request()
    var cookiePersistor = CookiePersistor("")
    private val cookieName = "epicture"


    /**
     * Prepare Instagram API
     */
    fun prepare() {
        val file = File(context.filesDir, cookieName)
        deviceId = Crypto.generateDeviceId(file.absolutePath)
        uuid = Crypto.randomUUID(true)
        cookiePersistor = CookiePersistor(file.absolutePath)
        if (cookiePersistor.exist()) {
            val cookieDisk = cookiePersistor.load()
            val account = JSONObject(cookieDisk.account)
            if (account.getString("status").toLowerCase() == "ok") {
                println("Already login to Instagram")
                val jar = cookieDisk.cookieJar
                Req.persistedCookies = jar
                isLogin = true
                ds_user_id = jar.getCookie("ds_user_id")?.value.toString()
                token = jar.getCookie("csrftoken")?.value.toString()
                rankToken = "${ds_user_id}_$uuid"
            }
        }
    }

    /**
     * Function for login to instagram, if force=true API will not use saved cookies
     */
    fun login(force: Boolean = false) {
        if (!isLogin || force) {
            val payload = """{"_csrftoken":"missing","device_id":"$deviceId","_uuid":"$uuid","username":"$username","password":"$password","login_attempt_count":0}"""
            val response = Req.prepare(Routes.login(), payload).send()
            cookiePersistor.save(response.text, response.cookies)
            println("Instagram.kt ${response.text}")
            println("Instagram.kt ${response.cookies.entries}")
            val account = response.jsonObject
            if (account.getString("status").toLowerCase().equals("ok")) {
                println("Already login to Instagram")
                val jar = response.cookies
                isLogin = true
                ds_user_id = jar.getCookie("ds_user_id")?.value.toString()
                token = jar.getCookie("csrftoken")?.value.toString()
                rankToken = "${ds_user_id}_$uuid"
                println("Instagram login success")
            }
        }
    }

    /**
     * Function to logout from instagram
     */
    fun logout(): Response {
        val response = Req.prepare(Routes.logout()).send()
        cookiePersistor.destroy()
        return response
    }

    /**
     * Do SyncFeature
     */
    fun syncFeature(): Response {
        return Req.prepare(Routes.qeSync()).send()
    }

    /**
     * Get autcomplete user list
     */
    fun getAutoCompleteUserList(): Response {
        return Req.prepare(Routes.autocompleteUserList()).send()
    }

    /**
     * Get timeline feed
     */
    fun getTimelineFeed(): Response {
        return Req.prepare(Routes.timelineFeed()).send()
    }

    fun getv2Inbox(): Response {
        return Req.prepare(Routes.v2Inbox()).send()
    }

    fun getRecentActivity(): Response {
        return Req.prepare(Routes.recetActivity()).send()
    }

    fun getUserFeed(): Response {
        return Req.prepare(Routes.userFeed(ds_user_id, "", ds_user_id)).send()
    }

    fun likeContent(id: String): Response {
        println("token is $token")
        return Req.prepare(Routes.like(id, token), token).send()
    }

    fun tagFeed(tag: String): Response {
        return Req.prepare(Routes.tagFeed(tag, "", rankToken)).send()
    }
    
    fun hashtagsSearch(query: String): Response {
        return Req.prepare(Routes.hashtagsSearch(query, rankToken)).send()
    }
}