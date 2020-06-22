package com.example.absensi.common

import android.content.Context
import android.content.SharedPreferences

class Preferences(context: Context) {
    private var sharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor
    private val ACCESS_TOKEN = "access_token"
    private val FIRST_LAUNCH = "first_launch"
    private val USER_LOGGED_IN = "logged_in"
    private val USER_PHONE = "phone_number"
    private val BUSINESS_ID = "business_id"
    private val IS_FETCH_RECYCLER_LIST = "is_fetch_recycler_list"
    private val AFTER_CHECK_IN_SUCCESS = "after_check_in"
    private val AFTER_CHECK_OUT_SUCCESS = "after_check_out"

    init {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun userLoggedOut() {
        editor.clear()
        userLoggedIn = false
    }


    var accessToken: String
        set(value) = editor.putString(ACCESS_TOKEN, value).apply()
        get() = sharedPreferences.getString(ACCESS_TOKEN, "") ?: ""

    var firstLaunch: Boolean
        set(value) = editor.putBoolean(FIRST_LAUNCH, value).apply()
        get() = sharedPreferences.getBoolean(FIRST_LAUNCH, true)

    var userLoggedIn: Boolean
        set(value) = editor.putBoolean(USER_LOGGED_IN, value).apply()
        get() = sharedPreferences.getBoolean(USER_LOGGED_IN, false)

    var phone: String
        set(value) = editor.putString(USER_PHONE, "0$value").apply()
        get() = sharedPreferences.getString(USER_PHONE, "") ?: ""

    var businessId: String
        set(value) = editor.putString(BUSINESS_ID, value).apply()
        get() = sharedPreferences.getString(BUSINESS_ID, "") ?: ""

    var isFetchRecyclerList: Boolean
        set(value) = editor.putBoolean(IS_FETCH_RECYCLER_LIST, value).apply()
        get() = sharedPreferences.getBoolean(IS_FETCH_RECYCLER_LIST, false)

    var isFetchDetailUpdates: Boolean
        set(value) = editor.putBoolean(IS_FETCH_RECYCLER_LIST, value).apply()
        get() = sharedPreferences.getBoolean(IS_FETCH_RECYCLER_LIST, false)

    var afterCheckInSuccess: Boolean
        set(value) = editor.putBoolean(AFTER_CHECK_IN_SUCCESS, value).apply()
        get() = sharedPreferences.getBoolean(AFTER_CHECK_IN_SUCCESS, false)

    var afterCheckOutSuccess: Boolean
        set(value) = editor.putBoolean(AFTER_CHECK_OUT_SUCCESS, value).apply()
        get() = sharedPreferences.getBoolean(AFTER_CHECK_OUT_SUCCESS, false)
}
