package com.michel.team.epicture.api

import khttp.get
import khttp.post
import khttp.responses.Response
import khttp.structures.cookie.CookieJar

class Request {
    var url = ""
    var data = ""
    var isGet = true
    var persistedCookies: CookieJar? = null
    var headers = HTTP.HEADERS

    fun prepare(reqUrl: String, jsonString: String = ""): Request {
        url = "${HTTP.ENDPOINT}$reqUrl"
        //url = "http://localhost/cookies?fuck=${url}";
        data = jsonString
        isGet = data.isNullOrEmpty()
        return this
    }


    fun send(): Response {
        //println("Cookies for ${url} is ${persistedCookies?.size} -> ${persistedCookies?.entries}")
        val resp: Response
        if (isGet) {
            resp = if (persistedCookies == null) {
                get(url, headers = headers)
            } else {
                get(url, headers = headers, cookies = persistedCookies)
            }
        } else {
            val signature = data.let { Crypto.signData(data) }
            val payload = mapOf("signed_body" to "${signature.signed}.${signature.payload}", "ig_sig_key_version" to signature.sigKeyVersion)
            //println("Request.kt PayLoad = $payload")
            resp = if (persistedCookies == null) {
                post(url, headers = headers, data = payload)
            } else {
                println("====================================== $url, $headers, $payload, $persistedCookies")
                post(url, headers = headers, data = payload, cookies = persistedCookies)
            }
        }
        if (persistedCookies == null) {
            persistedCookies = resp.cookies
        } else {
            persistedCookies!!.putAll(resp.cookies)
        }
        return resp
    }
}